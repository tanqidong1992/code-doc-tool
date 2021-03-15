package com.hngd.parser.source;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.hngd.exception.SourceParseException;
import com.hngd.parser.entity.ClassInfo;
import com.hngd.parser.entity.FieldInfo;
import com.hngd.parser.entity.MethodInfo;
import com.hngd.parser.javadoc.BlockTag;
import com.hngd.parser.javadoc.MainDescription;
import com.hngd.parser.javadoc.JavaDocCommentElement;
import com.hngd.parser.javadoc.JavaDocCommentParser;
import com.hngd.utils.JavaParserUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SourceVisitor extends VoidVisitorAdapter<SourceVisitorContext>{
    @Override
    public void visit(ClassOrInterfaceDeclaration classOrInterface, SourceVisitorContext context) {

        doParseClassJavaDocComment(classOrInterface,context);
        classOrInterface.getChildNodes().stream()
            .filter(n->filterAndParseFieldComment(n,context))
            .filter(n -> n instanceof MethodDeclaration)
            .map(MethodDeclaration.class::cast)
            .forEach(m->parseMethodComment(m,context));
        
        super.visit(classOrInterface, context);
    }
    
    public static boolean doParseClassJavaDocComment(ClassOrInterfaceDeclaration classOrInterfaceDeclaration,SourceVisitorContext context){
        Comment comment = classOrInterfaceDeclaration.getComment().orElse(null);
        String classFullName=getNodeFullName(classOrInterfaceDeclaration, context);
        if (comment instanceof JavadocComment) {
            JavadocComment javadocComment = (JavadocComment) comment;
            String content = javadocComment.getContent();
            String commentLines[] = content.split("\n");
            ClassInfo ci=new ClassInfo();
            List<JavaDocCommentElement> commentElements = JavaDocCommentParser.parse(Arrays.asList(commentLines));
            Optional<MainDescription> optionalDescription=commentElements.stream()
                .filter(commentElement->commentElement instanceof  MainDescription)
                .map(MainDescription.class::cast)
                .findFirst();
            optionalDescription.ifPresent(description->{
                String classOrInterfaceName=classOrInterfaceDeclaration.getNameAsString();
                ci.setName(classOrInterfaceName);
                ci.setComment(description.getContent());
                //ci.setClassOrInterfaceDetail(classOrInterfaceDeclaration);
                context.saveClassComment(classFullName, ci);
            });
            commentElements.stream()
                .filter(BlockTag.class::isInstance)
                .map(BlockTag.class::cast)
                .forEach(bt->bt.onParseEnd(ci));
            
        }else{
            log.debug("javadoc comment for classOrInterface {} is not found",classFullName);
        }
        return true;
    }
    
    private static void parseMethodComment(MethodDeclaration method, SourceVisitorContext context){
        String fullMethodName=getNodeFullName(method, context);
        String methodName = method.getName().asString();
        String className = ((ClassOrInterfaceDeclaration) method.getParentNode().get()).getName().asString();
        methodName = className + "#" + methodName;
        Comment comment = method.getComment().orElse(null);
        if (comment instanceof JavadocComment) {
            JavadocComment javadocComment = (JavadocComment) comment;
            String content = javadocComment.getContent();
            try {
                MethodInfo mi=doParseMethodJavaDocComment(method, methodName, content); 
                context.saveMethodComment(fullMethodName,mi);
            }catch(Throwable e) {
                String msg="Parsing Method:"+fullMethodName+" Failed!";
                throw new SourceParseException(msg,e);
            }
        } else {
            log.debug("method {} has no javadoc comment",fullMethodName);
        }
    }
    
    private static MethodInfo doParseMethodJavaDocComment(MethodDeclaration m, String methodName, String content) {
        MethodInfo mi = new MethodInfo();
        mi.setName(methodName);
        String commentLines[] = content.split("\n");
        List<JavaDocCommentElement> commentElements = JavaDocCommentParser.parse(Arrays.asList(commentLines));
        //set block tags
        commentElements.stream()
            .filter(BlockTag.class::isInstance)
            .map(BlockTag.class::cast)
            .forEach(cb->cb.onParseEnd(mi));
        //set description
        commentElements.stream()
            .filter(MainDescription.class::isInstance)
            .map(MainDescription.class::cast)
            .forEach(d->{
                mi.setComment(d.getContent());
            });
        
        return mi;
    }
    
    private static boolean filterAndParseFieldComment(Node n, SourceVisitorContext context) {
        
        if(!(n instanceof FieldDeclaration)) {
            return true;
        }
        String fullFieldName=getNodeFullName(n, context);
        FieldDeclaration field = (FieldDeclaration) n;
        String fieldName =extractFieldName(field);
        if(fieldName==null) {
            return true;
        }
        String trimComment=extractFieldComment(field);
        if (trimComment.length() > 0) {
            context.saveFieldComment(fullFieldName, new FieldInfo(trimComment, fieldName, field));
        } else {
            log.debug("The java document comment for field:{} is empty",fullFieldName);
        }
        return true;
    }
    
    public static String getNodeFullName(Node node,SourceVisitorContext context) {
        String name=JavaParserUtils.getParentNodeNameList(node);
        String packageName=context.getPackageName();
        return StringUtils.isBlank(packageName)?name:packageName+"."+name;
    }
    
    private static String extractFieldName(FieldDeclaration field) {
        NodeList<VariableDeclarator> variables=field.getVariables();
        if(CollectionUtils.isEmpty(variables)) {
            log.error("The veriables for field:{} is empty",field);
            return null;
        }
        VariableDeclarator variable = field.getVariables().get(0);
        SimpleName variableSimpleName = variable.getName();
        return variableSimpleName.toString();
    }
    private static String extractFieldComment(FieldDeclaration field) {
        Comment comment = field.getComment().orElse(null);
        if (!(comment instanceof JavadocComment)) {
            return "";
        }
        JavadocComment jdoc = (JavadocComment) comment;
        String content = jdoc.getContent();
        StringBuilder trimComment = new StringBuilder();
        String[] lines = content.split("\n");
        if (lines != null && lines.length > 0) {
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i].trim();
                line = line.replaceFirst("\\*", "");
                line=line.trim();
                if (!StringUtils.isEmpty(line)) {
                    trimComment.append(line);
                }
            }
        }
        return trimComment.toString();
    }
  
}
