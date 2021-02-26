package com.hngd.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.Problem;
import com.github.javaparser.ast.CompilationUnit;
import com.hngd.exception.SourceParseException;
import com.hngd.openapi.OpenAPITool;

public class ClassUtils {

    private static final Logger logger=LoggerFactory.getLogger(ClassUtils.class);
    public static List<Class<?>> getClassBelowPacakge(String packageName) {
        String packagePath = packageName.replaceAll("\\.", "/");
        Enumeration<URL> dirs = null;
        try {
            dirs = OpenAPITool.class.getClassLoader().getResources(packagePath);
        } catch (IOException e) {
            logger.error("", e);
        }

        List<Class<?>> clazzs = new LinkedList<>();
        while (dirs.hasMoreElements()) {
            URL url = dirs.nextElement();
            File file = new File(url.getFile());
            // 把此目录下的所有文件列出
            String[] fileNames = file.list();
            // 循环此数组，并把.class去掉
            for (String fileName : fileNames) {
                if (!fileName.endsWith(".class")) {
                    logger.warn("the file {} is not a class",file.getAbsolutePath() + fileName );
                    continue;
                }
                fileName = fileName.substring(0, fileName.length() - 6);
                // 拼接上包名，变成全限定名
                String qName = packageName + "." + fileName;
                // 如有需要，把每个类生实一个实例
                Class<?> cls = null;
                try {
                    cls = Class.forName(qName);
                } catch (ClassNotFoundException e) {
                    logger.error("", e);
                }
                if (cls != null) {
                    clazzs.add(cls);
                }
            }
        }
        return clazzs;
    }
    public static Boolean isClassDeprecated(Class<?> cls) {
        return cls.getAnnotation(Deprecated.class)!=null;
    }
    public static CompilationUnit parseClass(File f) {
        
        ParseResult<CompilationUnit> cu=null;
        try(InputStream in=new FileInputStream(f)) {
            Reader reader=new BufferedReader(new InputStreamReader(in, "UTF-8"));
            cu = new JavaParser().parse(reader);//.parse(reader, true);
        } catch (IOException e) {
            logger.error("", e);
        }
        return cu.getResult().get();
    }
    
    public static CompilationUnit parseClass(InputStream in) {
        ParseResult<CompilationUnit> cu=null;
        try {
            Reader reader=new BufferedReader(new InputStreamReader(in, "UTF-8"));
            cu = new JavaParser().parse(reader);//.parse(reader, true);
        } catch (IOException e) {
            logger.error("", e);
        }
        if(cu.isSuccessful()) {
            return cu.getResult().get();
        }else {
            List<Problem> problems=cu.getProblems();
            Problem problem=problems.get(0);
            throw new SourceParseException(problem.getMessage(),
                    problem.getCause().orElse(null));
        }  
    }

    public static String getMethodIdentifier(Method method) {
        if(method==null) {
            return "";
        }
        return method.getDeclaringClass().getName()+"."+method.getName();
    }
    
    public static String getParameterIdentifier(Parameter parameter) {
        if(parameter==null) {
            return "";
        }
        Executable executable=parameter.getDeclaringExecutable();
        String name=parameter.getName();
        if(executable instanceof Method) {
            return getMethodIdentifier((Method)executable)+"."+name;
        }else if(executable instanceof Constructor) {
            Constructor<?> c=(Constructor<?>) executable;
            return c.getDeclaringClass().getName()+"."+c.getName()+"."+name;
        }
        return executable.getDeclaringClass().getName()+"."+executable.getName()+"."+name;
    }
}
