package com.hngd.tool.cl;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hngd.classloader.ProjectClassLoader;
import com.hngd.tool.RestJavaAPIGenerator;

public class JavaAPICodeGeneratorTest {

    public static void main(String[] args) throws ClassNotFoundException, MalformedURLException, IOException {
        
        String path="E:\\workspaces\\hnvmns-code-modules\\inspection-system\\target\\inspection-system-0.0.1-SNAPSHOT.jar";
        path="D:\\company\\projects\\inspection-system\\inspection-system\\target\\inspection-system-0.0.1-SNAPSHOT.jar";
        Logger logger=LoggerFactory.getLogger("XX");
        ProjectClassLoader loader=new ProjectClassLoader(JavaAPICodeGeneratorTest.class.getClassLoader());
        loader.addClasspath(path);
        File outDir=new File("./api/java");
        if(outDir.exists() || outDir.mkdirs()) {
            
        }
        
        String packageFilter="com.hngd.web.controller.";
        String apiPackage="com.hngd.api";
        RestJavaAPIGenerator.generateJavaAPIFile(packageFilter,"async","baseurl", apiPackage, outDir, Arrays.asList(new File(path)));
        /*
         * List<String> allClass=loader.listAllClass("default"); allClass.stream()
         * .filter(name->name.startsWith("com.hngd.web.controller.")) .map(name->{ try {
         * return loader.loadClass(name,true); } catch (ClassNotFoundException e) { //
         * TODO Auto-generated catch block e.printStackTrace(); } return null; })
         * .filter(clazz -> clazz != null) //.filter(clazz ->
         * clazz.getAnnotation(RequestMapping.class) != null) .forEach(cls->{
         * for(Annotation a:cls.getAnnotations()){ Class<?> acls=a.annotationType();
         * Class<?>
         * restCls=org.springframework.web.bind.annotation.RestController.class;
         * System.out.println(acls+
         * ":=org.springframework.web.bind.annotation.RestController:"+(RestController.
         * class==acls));
         * System.out.println(acls.getClassLoader()+":"+restCls.getClassLoader()); }
         * JavaAPICodeGenerator.generateJavaAPIFile(cls,"com.hngd.api",outDir.
         * getAbsolutePath()); });
         */
    }

}
