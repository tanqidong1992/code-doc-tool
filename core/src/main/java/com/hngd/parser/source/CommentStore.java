package com.hngd.parser.source;

import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import com.hngd.parser.entity.ClassInfo;
import com.hngd.parser.entity.FieldInfo;
import com.hngd.parser.entity.MethodInfo;
import com.hngd.parser.entity.ParameterInfo;

public class CommentStore {

    private  Map<String, ClassInfo> classComments = new ConcurrentHashMap<>();
    private  Map<String, MethodInfo> methodComments = new ConcurrentHashMap<>();
    private  Map<String, FieldInfo> fieldComments = new ConcurrentHashMap<>();
    
    public void save(SourceParseResult result) {
        result.getClassComments().forEach((k,v)->{
            this.classComments.put(k, v);
        });
        result.getFieldComments().forEach((k,v)->{
            this.fieldComments.put(k, v);
        });
        result.getMethodComments().forEach((k,v)->{
            this.methodComments.put(k, v);
        });
    }
    
    
    public  String getMethodComment(String className, String methodName) {
        String key = className + "." + methodName;
        MethodInfo mi = methodComments.get(key);
        return mi != null ? mi.getComment() : null;
    }
    public  Optional<MethodInfo> getMethodInfo(Executable method) {
        String key = methodKey(method);
        MethodInfo mi = methodComments.get(key);
        return Optional.ofNullable(mi);
    }
    
    public  String getMethodComment(Executable method) {
        String key = methodKey(method);
        MethodInfo mi = methodComments.get(key);
        return mi != null ? mi.getComment() : null;
    }
    public  String getFieldComment(Field field) {
        String key = fieldKey(field);
        FieldInfo mi = fieldComments.get(key);
        return mi != null ? mi.getComment() : null;
    }
    public  String getFieldComment(String className, String fieldName) {
        String key = className + "." + fieldName;
        FieldInfo mi = fieldComments.get(key);
        return mi != null ? mi.getComment() : null;
    }
    
    public  String getParameterComment(String className, String methodName, String parameterName) {
        String key = className + "." + methodName;
        MethodInfo mi = methodComments.get(key);
        String comment = null;
        if (mi != null) {
            Optional<ParameterInfo> op = mi.getParameters().stream()
                    .filter(p -> p.getName().equals(parameterName))
                    .findFirst();
            if (op.isPresent()) {
                comment = op.get().getComment();
            }
        }
        return comment;
    }

    public  String getParameterComment(String className, String methodName, int order) {
        String key = className + "." + methodName;
        MethodInfo mi = methodComments.get(key);
        String comment = null;
        if (mi != null) {
            if (order >= 0 && order < mi.getParameters().size()) {
                comment = mi.getParameters().get(order).getComment();
            }
        }
        return comment;
    }
    
    public  void print() {
        classComments.entrySet()
        .forEach(e->{
            System.out.println(e.getKey()+"-->"+e.getValue());
        });
        fieldComments.entrySet()
        .forEach(e->{
            System.out.println(e.getKey()+"-->"+e.getValue().getComment());
        });
        methodComments.entrySet()
        .forEach(e->{
            System.out.println(e.getKey()+"-->"+e.getValue().getComment());
            e.getValue().getParameters().forEach(p->{
                System.out.println("    "+p.getName()+"-->"+p.getComment());
            });
        });
        
    }
    
    public static  String classKey(Class<?> clazz) {
        String s="";
        Class<?> enclosingClass=clazz.getEnclosingClass();
        while(enclosingClass!=null) {
            String enclosingClassName=enclosingClass.getSimpleName();
            s=enclosingClassName+"."+s;
            enclosingClass=enclosingClass.getEnclosingClass();
        }
        if(StringUtils.isNotEmpty(s)) {
            String packageName=clazz.getPackage().getName();
            return packageName+"."+s+clazz.getSimpleName();
        }else {
            return clazz.getName();
        }
        
    }
    public  static String methodKey(Executable method) {
        Class<?> clazz=method.getDeclaringClass();
        return classKey(clazz)+"."+method.getName();
    }
    public  static String fieldKey(Field field) {
        Class<?> clazz=field.getDeclaringClass();
        return classKey(clazz)+"."+field.getName();
    }
    public  static String parameterKey(Parameter parameter) {
        Executable method=parameter.getDeclaringExecutable();
        return methodKey(method)+"."+parameter.getName();
    }

    public ClassInfo getClassComment(Class<?> cls) {
        String classKey=classKey(cls);
        return classComments.get(classKey);
    }
}
