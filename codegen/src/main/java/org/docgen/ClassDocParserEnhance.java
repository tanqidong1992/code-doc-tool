/**
 * Copyright (c) 2015,湖南华南光电科技股份有限公司
 * All rights reserved.
 *
 * @文件名：ClassDocParserEnhance.java
 * @时间：2016年8月12日 下午3:55:27
 * @作者：
 * @备注：
 * @版本:
 */

package org.docgen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.docgen.ClassDocParser.DFieldInfo;
import org.docgen.ClassDocParser.DMethodInfo;
import org.docgen.ClassDocParser.DParameterInfo;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.Node;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.body.VariableDeclaratorId;
import japa.parser.ast.comments.Comment;
import japa.parser.ast.comments.JavadocComment;
import japa.parser.ast.comments.LineComment;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.type.Type;

/**
 * @author
 */
public class ClassDocParserEnhance
{
    static class DClassInfo
    {
        String className;
        Map<String, DFieldInfo>  parameters = new HashMap<>();
        Map<String, DMethodInfo> methods    = new HashMap<>();
    }
static boolean ignoreComment = false;
    public static void main(String[] args) throws ParseException, IOException
    {
        File controllers = new File(Config.controllerRoot);
        
        
        
        Arrays.asList(controllers.listFiles())
        .stream()
        .sorted()
        .filter(f->f.getName().endsWith(".java"))
        .map(f->parseClass(f))
        .filter(f->f!=null)
        .forEach(ci->{
            
            System.out.println("###" +ci.className);
            
            System.out.println("####属性");
            System.out.println(DFieldInfo.toSimpleHeader());
            ci.parameters.values().stream()
            .sorted((m1,m2)->{
                if(m1.order>m2.order){
                    
                    return 1;
                }else{
                    
                    return -1;
                }
                 
            })
            .forEach(m ->
            {
                System.out.println(m.toSimpleData());
            });
            
            
            System.out.println("####方法");
            System.out.println(DMethodInfo.toSimpleHeader());
             
            ci.methods.values().stream()
            .sorted((m1,m2)->{
                if(m1.order>m2.order){
                    
                    return 1;
                }else{
                    
                    return -1;
                }
                 
            })
            .forEach(m ->
            {
                System.out.println(m.toSimpleData().replaceAll("包内访问", "公开"));
            });
        });
       
        
        File services = new File(Config.serviceRoot);
         
        Arrays.asList(services.listFiles())
        .stream()
        .sorted()
        .filter(f->f.getName().endsWith(".java"))
        .map(f->parseClass(f))
        .filter(f->f!=null)
        .forEach(ci->{
            
            System.out.println("###" +ci.className);
            
            System.out.println("####属性");
            System.out.println(DFieldInfo.toSimpleHeader());
            ci.parameters.values().stream()
            .sorted((m1,m2)->{
                if(m1.order>m2.order){
                    
                    return 1;
                }else{
                    
                    return -1;
                }
                 
            })
            .forEach(m ->
            {
                System.out.println(m.toSimpleData());
            });
            
            
            System.out.println("####方法");
            System.out.println(DMethodInfo.toSimpleHeader());
            ci.methods.values().stream()
            .sorted((m1,m2)->{
                if(m1.order>m2.order){
                    
                    return 1;
                }else{
                    
                    return -1;
                }
                 
            })
            .forEach(m ->
            {
                System.out.println(m.toSimpleData().replaceAll("包内访问", "公开"));
            });
        });
       
        File daos = new File(Config.daoRoot);
        ignoreComment=true;
        Arrays.asList(daos.listFiles())
        .stream()
        .sorted()
        .filter(f->f.getName().endsWith(".java"))
        .map(f->parseClass(f))
        .filter(f->f!=null)
        .forEach(ci->{
            
            System.out.println("###" +ci.className);
            
            System.out.println("####属性");
            System.out.println(DFieldInfo.toSimpleHeader());
            ci.parameters.values().stream()
            .sorted((m1,m2)->{
                if(m1.order>m2.order){
                    
                    return 1;
                }else{
                    
                    return -1;
                }
                 
            })
            .forEach(m ->
            {
                System.out.println(m.toSimpleData());
            });
            
            
            System.out.println("####方法");
            System.out.println(DMethodInfo.toSimpleHeader());
            ci.methods.values().stream()
            .sorted((m1,m2)->{
                if(m1.order>m2.order){
                    
                    return 1;
                }else{
                    
                    return -1;
                }
                 
            })
            .forEach(m ->
            {
                System.out.println(m.toSimpleData().replaceAll("包内访问", "公开"));
            });
        });
        
    }

    public static DClassInfo parseClass(File f)  
    {
        final DClassInfo classInfo = new DClassInfo();
        classInfo.className=f.getName().replace(".java", "");
        CompilationUnit cu = null;
        try
        {
            cu = JavaParser.parse(f);
        } catch (ParseException | IOException e)
        {
             
            e.printStackTrace();
            
           
        }
        if(cu==null){
            
            return null;
        }
        List<Node> nodes = cu.getChildrenNodes();
        nodes.stream().filter(n -> n instanceof ClassOrInterfaceDeclaration).flatMap(n -> n.getChildrenNodes().stream())
                .filter(n -> n instanceof MethodDeclaration).forEach(n ->
                {
                    MethodDeclaration m = (MethodDeclaration) n;
                    String methodName = m.getName();
                    JavadocComment jdoc = null;
                    LineComment lDoc=null;
                    Comment comment=m.getComment();
                    
                    if(comment instanceof JavadocComment){
                        
                        jdoc = (JavadocComment) comment;
                    }else if(comment instanceof LineComment){
                        
                        lDoc=(LineComment) comment;
                    }else{
                        //gg
                    }
                    if (jdoc != null)
                    {
                        String content = jdoc.toString();
                        parseMethod(m, methodName, content, classInfo.methods);
                    }
                });
        nodes.stream().filter(n -> n instanceof ClassOrInterfaceDeclaration).flatMap(n -> n.getChildrenNodes().stream())
                .filter(n -> n instanceof FieldDeclaration).forEach(n ->
                {
                    FieldDeclaration fd = (FieldDeclaration) n;
                    parseField(fd, classInfo.parameters);
                });
        return classInfo;
    }

    /**
     * @param fd
     * @param parameters
     * @author
     * @since 0.0.1
     */
    public static void parseField(FieldDeclaration fd, Map<String, DFieldInfo> parameters)
    {
        DFieldInfo fieldInfo = new DFieldInfo();
        fieldInfo.modifiers = fd.getModifiers();
        Type type = fd.getType();
        fieldInfo.typeName = type.toString();
        List<VariableDeclarator> list = fd.getVariables();
        VariableDeclarator vd = list.get(0);
        Expression expression = vd.getInit();
        if (expression == null)
        {
            fieldInfo.initValue = "null";
        } else
        {
            fieldInfo.initValue = fieldInfo.typeName + "实例";
        }
        fieldInfo.name = vd.getId().getName();
        fieldInfo.order=parameters.size()+1;
        parameters.put(fieldInfo.name, fieldInfo);
    }

    private static void parseMethod(MethodDeclaration m, String methodName, String content,
            Map<String, DMethodInfo> methodComments)
    {
        // TODO Auto-generated method stub
        content = content.replace("*", "").replace("/", "");
        String commentLines[] = content.split("\n");
        List<String> goodLines = new ArrayList<String>();
        for (int i = 0; i < commentLines.length; i++)
        {
            String line = commentLines[i];
            line = line.trim();
            if (line.length() <= 0)
            {
                continue;
            }
            goodLines.add(line);
        }
        DMethodInfo mi = new DMethodInfo();
        mi.name = methodName;
        mi.retType = m.getType().toString();
        mi.modifiers = m.getModifiers();
        String className = ((ClassOrInterfaceDeclaration) m.getParentNode()).getName();
        mi.enclosingClass = className;
        List<Parameter> parameters = m.getParameters();
        if (parameters != null)
        {
            parameters.forEach(p ->
            {
                VariableDeclaratorId variableId = p.getId();
                Type type = p.getType();
                DParameterInfo pi = new DParameterInfo();
                pi.name = variableId.getName();
                pi.type = type.toString();
                mi.parameters.put(pi.name, pi);
            });
        }
        goodLines.forEach(l ->
        {
            if (!l.startsWith("@"))
            {
                if (mi.comment == null)
                {
                    mi.comment = l;
                }
            } else
            {
                if (l.startsWith("@param"))
                {
                    l = l.replace("@param", "").trim();
                    if (l.contains("@see"))
                    {
                    } else
                    {
                        String items[] = l.split(" ");
                        if (items.length > 1)
                        {
                            String pn = items[0];
                            String pc = items[1];
                            DParameterInfo pi = mi.parameters.get(pn);
                            if (pi != null)
                            {
                                pi.comment = pc;
                            } else
                            {
                                // System.err.println("cannot find "+pn+" in
                                // "+mi.name);
                            }
                        } else
                        {
                            // System.err.println("error in "+l);
                        }
                    }
                } else if (l.startsWith("@return"))
                {
                    l = l.replace("@return", "");
                    mi.retComment = l;
                }
            }
        });
        mi.order=methodComments.size()+1;
        methodComments.put(methodName, mi);
    }
}
