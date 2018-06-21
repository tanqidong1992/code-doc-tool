
package com.hngd.doc.core.parse;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.hngd.doc.core.FieldInfo;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.Node;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.body.VariableDeclaratorId;
import japa.parser.ast.comments.Comment;
import japa.parser.ast.comments.JavadocComment;
import japa.parser.ast.type.Type;

public class EntityClassCommentParser
{
    private static Logger                logger        = Logger.getLogger(EntityClassCommentParser.class);
    public static Map<String, FieldInfo> fieldComments = new HashMap<String, FieldInfo>();

    public static void init(String[] roots)
    {
        for (String root : roots)
        {
            File file = new File(root);
            parseFile(file);
        }
    }

    private static void parseFile(File file)
    {
        if (file.exists() && file.isDirectory())
        {
            File files[] = file.listFiles();
            Arrays
                    .asList(files)
                    .stream()
                    .filter(f -> f.getName().endsWith(".java") && !f.getName().endsWith("Example.java"))
                    .forEach(f ->
                    {
                        try
                        {
                            parse(f);
                        } catch (Exception e)
                        {
                            logger.error("", e);
                        }
                    });
        }
    }

    private static void parse(File f) throws ParseException, IOException
    {
        logger.info("parsing " + f.getAbsolutePath());
        CompilationUnit cu = JavaParser.parse(f);
        List<Node> nodes = cu.getChildrenNodes();
        nodes.stream().filter(n -> n instanceof ClassOrInterfaceDeclaration).flatMap(n ->
        {
            return n.getChildrenNodes().stream();
        }).filter(n -> n instanceof FieldDeclaration).forEach(n ->
        {
            FieldDeclaration f1 = (FieldDeclaration) n;
            VariableDeclarator variable = f1.getVariables().get(0);
            if (variable != null)
            {
                VariableDeclaratorId variableId = variable.getId();
                if (variableId != null)
                {
                    String variableName = variableId.toString();
                    // Type type = f1.getType();
                    Comment comment = n.getComment();
                    if (comment instanceof JavadocComment)
                    {
                        JavadocComment jdoc = (JavadocComment) comment;
                        String content = jdoc.getContent();
                        StringBuilder trimComment = new StringBuilder();
                        String[] lines = content.split("\n");
                        if (lines != null && lines.length > 0)
                        {
                            for (int i = 0; i < lines.length; i++)
                            {
                                String line = lines[i].trim();
                                line = line.replaceFirst("\\*", "");
                                if (!StringUtils.isEmpty(line))
                                {
                                    trimComment.append(line);
                                }
                            }
                        }
                        ClassOrInterfaceDeclaration c = (ClassOrInterfaceDeclaration) f1.getParentNode();
                        if (trimComment.length() > 0)
                        {
                            String key = c.getName() + "#" + variableName;
                            fieldComments.put(key, new FieldInfo(trimComment.toString(), variableName, f1));
                        }
                    } else
                    {
                        logger.error(f1 + " no jdoc" + comment);
                    }
                } else
                {
                    logger.error("variableId is null");
                }
            } else
            {
                logger.error("variable is null");
            }
        });
    }

    public static void main(String[] args)
    {
        System.out.println(fieldComments.size());
    }
}
