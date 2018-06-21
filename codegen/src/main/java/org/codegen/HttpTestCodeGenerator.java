
package org.codegen;

import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import javax.lang.model.element.Modifier;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import com.hngd.doc.core.InterfaceInfo;
import com.hngd.doc.core.InterfaceInfo.ParamType;
import com.hngd.doc.core.InterfaceInfo.RequestParameterInfo;
import com.hngd.doc.core.ModuleInfo;
import com.hngd.doc.core.gen.SwaggerDocGenerator;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import rx.Observable;
/**
 * Hello world!
 */
public class HttpTestCodeGenerator
{
    public static void main(String[] args) throws URISyntaxException, ClassNotFoundException
    {
        File out = new File("..\\webapi-test\\src\\main\\java");
        String outPackageName="com.hngd.web.api";
        String packageName = "com.hngd.web.controller";
        URL url = HttpTestCodeGenerator.class.getResource("/com/hngd/web/controller");
        Path path = Paths.get(url.toURI());
        File[] files = path.toFile().listFiles();
        Arrays
                .asList(files)
                .stream()
                .map(file -> file.getName())
                .filter(name -> name.endsWith(".class"))
                .map(name -> name.replace(".class", ""))
                .map(name -> packageName + "." + name)
                .map(name ->
                {
                    Class<?> clazz = null;
                    try
                    {
                        clazz = Class.forName(name);
                    } catch (Exception e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    return clazz;
                })
                .filter(clazz -> clazz != null)
                .filter(clazz -> clazz.getAnnotation(RequestMapping.class) != null)
                .forEach(cls ->
                {
                    ModuleInfo moduleInfo = SwaggerDocGenerator.processClass(cls);
                    TypeSpec typeSpec = toJavaFile(moduleInfo);
                    JavaFile javaFile = JavaFile.builder(outPackageName, typeSpec).build();
                    try
                    {
                        javaFile.writeTo(out);
                    } catch (Exception e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                });
        // Class<?> cls;
        // SwaggerDocGenerator.processClass(cls);
    }

    /**
     * @param moduleInfo
     * @return
     * @author tqd
     * @since 1.0.0
     * @time 2017年3月16日 上午9:41:12
     */
    private static TypeSpec toJavaFile(ModuleInfo moduleInfo)
    {
        String name = moduleInfo.className;
        TypeSpec.Builder builder = TypeSpec.interfaceBuilder(name).addModifiers(Modifier.PUBLIC);
        for (InterfaceInfo ii : moduleInfo.interfaceInfos)
        {
            MethodSpec.Builder mb = MethodSpec.methodBuilder(ii.methodName).addModifiers(Modifier.PUBLIC,
                    Modifier.ABSTRACT);
            Class<?> aclazz = null;
            if (ii.requestType.equals("POST"))
            {
                aclazz = POST.class;
                if (ii.isMultipart)
                {
                    mb.addAnnotation(Multipart.class);
                } else
                {
                    mb.addAnnotation(FormUrlEncoded.class);
                }
            }else
            if (ii.requestType.equals("GET"))
            {
                aclazz = GET.class;
            }
            AnnotationSpec annotationSpec = AnnotationSpec
                    .builder(aclazz)
                    .addMember("value", "\"" + moduleInfo.moduleUrl.substring(1) + ii.methodUrl + "\"")
                    .build();
            mb.addAnnotation(annotationSpec);
            for (int i = 0; i < ii.parameterNames.size(); i++)
            {
                RequestParameterInfo rpi = ii.parameterNames.get(i);
                Type type = ii.parameterTypes.get(i);
                ParameterSpec.Builder pb = ParameterSpec.builder(type != MultipartFile.class ? String.class : RequestBody.class,
                        rpi.name);
                if (rpi.paramType.equals(ParamType.PATH))
                {
                    AnnotationSpec mbA = AnnotationSpec
                            .builder(retrofit2.http.Path.class)
                            .addMember("value", "\"" + rpi.name + "\"")
                            .build();
                    pb.addAnnotation(mbA);
                } else
                {
                    if (ii.requestType.equals("POST"))
                    {
                        if (MultipartFile.class == type)
                        {
                            AnnotationSpec mbA = AnnotationSpec
                                    .builder(retrofit2.http.Part.class)
                                    .addMember("value", "\"" + rpi.name + "\"")
                                    .build();
                            pb.addAnnotation(mbA);
                        } else if (ii.isMultipart)
                        {
                            AnnotationSpec mbA = AnnotationSpec
                                    .builder(retrofit2.http.Query.class)
                                    .addMember("value", "\"" + rpi.name + "\"")
                                    .build();
                            pb.addAnnotation(mbA);
                        } else
                        {
                            AnnotationSpec mbA = AnnotationSpec
                                    .builder(retrofit2.http.Field.class)
                                    .addMember("value", "\"" + rpi.name + "\"")
                                    .build();
                            pb.addAnnotation(mbA);
                        }
                    } else if (ii.requestType.equals("GET"))
                    {
                        AnnotationSpec mbA = AnnotationSpec
                                .builder(retrofit2.http.Query.class)
                                .addMember("value", "\"" + rpi.name + "\"")
                                .build();
                        pb.addAnnotation(mbA);
                    }
                }
                ParameterSpec parameterSpec = pb.build();
                mb.addParameter(parameterSpec);
            }
            if (ii.requestType.equals("POST") && ii.parameterNames.size() == 0)
            {
                ParameterSpec.Builder pb = ParameterSpec.builder(String.class, "emptyStr");
                AnnotationSpec mbA = AnnotationSpec
                        .builder(retrofit2.http.Field.class)
                        .addMember("value", "\"emptyStr\"")
                        .build();
                pb.addAnnotation(mbA);
                ParameterSpec parameterSpec = pb.build();
                mb.addParameter(parameterSpec);
            }
            Type returnType = new ParameterizedType()
            {
                @Override
                public Type getRawType()
                {
                    // TODO Auto-generated method stub
                    return Call.class;
                }

                @Override
                public Type getOwnerType()
                {
                    // TODO Auto-generated method stub
                    return null;
                }

                @Override
                public Type[] getActualTypeArguments()
                {
                    // TODO Auto-generated method stub
                    return new Type[]
                    { ii.retureType };
                }
            };
            mb.returns(returnType);
           // mb.addCode("return null;");
            builder.addMethod(mb.build());
        }
        return builder.build();
    }
}
