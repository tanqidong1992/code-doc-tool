
package com.hngd.codegen;

import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import javax.lang.model.element.Modifier;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import com.hngd.openapi.entity.HttpInterface;
import com.hngd.openapi.entity.HttpParameter;
import com.hngd.openapi.entity.ModuleInfo;
import com.hngd.parser.source.SourceParserContext;
import com.hngd.parser.spring.ClassParser;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;

/**
 * Hello world!
 */
public class JavaAPICodeGenerator {
	
	private static final Logger logger=LoggerFactory.getLogger(JavaAPICodeGenerator.class);

     public static boolean generateJavaCode(String invokeType,String packageName,String outPackageName,String outputDir) {
    	 
    	File out = new File(outputDir);
    	String packagePath="/"+packageName.replaceAll("\\.", "/");
    	URL url = JavaAPICodeGenerator.class.getResource("/com/hngd/web/controller");
 		Path path=null;
		try {
			path = Paths.get(url.toURI());
		} catch (URISyntaxException e) {
			logger.error("",e);
		}
 		File[] files = path.toFile().listFiles();
 		Arrays.asList(files).stream().map(file -> file.getName()).filter(name -> name.endsWith(".class"))
 				.map(name -> name.replace(".class", "")).map(name -> packageName + "." + name).map(name -> {
 					Class<?> clazz = null;
 					try {
 						clazz = Class.forName(name);
 					} catch (Exception e) {
 						logger.error("",e);
 					}
 					return clazz;
 				})
 				.filter(clazz -> clazz != null)
 				.filter(clazz -> clazz.getAnnotation(RequestMapping.class) != null)
 				.forEach(cls -> {
 					ModuleInfo moduleInfo = new ClassParser(new SourceParserContext().getCommentStore()).parseModule(cls).get();
 					TypeSpec typeSpec = toJavaFile(invokeType,moduleInfo);
 					JavaFile javaFile = JavaFile.builder(outPackageName, typeSpec).build();
 					try {
 						javaFile.writeTo(out);
 					} catch (Exception e) {
 						logger.error("",e);
 					}
 				});
 		return true;
     }
	/**
	 * @param moduleInfo
	 * @return
	 * @author tqd
	 * @since 1.0.0
	 * @time 2017年3月16日 上午9:41:12
	 */
	static TypeSpec toJavaFile(String invokeType,ModuleInfo moduleInfo) {
		String name=null;
		if(moduleInfo.getSimpleClassName().endsWith("Controller")) {
			name = moduleInfo.getSimpleClassName().replace("Controller", "Client");
		}else {
			name = moduleInfo.getSimpleClassName()+"Client";
		}
		TypeSpec.Builder builder = TypeSpec.interfaceBuilder(name).addModifiers(Modifier.PUBLIC);
		for (HttpInterface httpInterface : moduleInfo.getInterfaceInfos()) {
			MethodSpec.Builder mb = MethodSpec.methodBuilder(httpInterface.javaMethodName).addModifiers(Modifier.PUBLIC,
					Modifier.ABSTRACT);
			Class<?> aclazz = null;
			if (httpInterface.httpMethod.equalsIgnoreCase("POST")) {
				aclazz = POST.class;
				if (httpInterface.isMultipart) {
					mb.addAnnotation(Multipart.class);
				} else {
					mb.addAnnotation(FormUrlEncoded.class);
				}
			} else {
				String httpMethod=httpInterface.httpMethod.toUpperCase();
				try {
					aclazz=Class.forName("retrofit2.http."+httpMethod);
				} catch (ClassNotFoundException e) {
					logger.error("",e);
				}
			}
			AnnotationSpec annotationSpec = AnnotationSpec.builder(aclazz)
					.addMember("value", "\"" + moduleInfo.getUrl().substring(1) + httpInterface.url + "\"").build();
			mb.addAnnotation(annotationSpec);
			
			
			if(httpInterface.httpMethod.equalsIgnoreCase("POST")) {
				
				if(httpInterface.isMultipart()) {
					for (int i = 0; i < httpInterface.getHttpParameters().size(); i++) {
						HttpParameter rpi = httpInterface.getHttpParameters().get(i);
						Type type =rpi.getJavaType();
						ParameterSpec.Builder pb = ParameterSpec
								.builder(type != MultipartFile.class ? RequestBody.class : MultipartBody.Part.class, rpi.name);
						if (MultipartFile.class == type) {
							AnnotationSpec mbA = AnnotationSpec.builder(retrofit2.http.Part.class)
									//.addMember("value", "\"" + rpi.name + "\"")
									.build();
							pb.addAnnotation(mbA);
						} else {
							AnnotationSpec mbA = AnnotationSpec.builder(retrofit2.http.Part.class)
									.addMember("value", "\"" + rpi.name + "\"").build();
							pb.addAnnotation(mbA);
						}  
						mb.addParameter(pb.build());
					}
				}else {
					for (int i = 0; i < httpInterface.getHttpParameters().size(); i++) {
						HttpParameter rpi = httpInterface.getHttpParameters().get(i);
						Type type =rpi.getJavaType();
						ParameterSpec.Builder pb = ParameterSpec
								.builder(String.class, rpi.name);
						AnnotationSpec mbA = AnnotationSpec.builder(retrofit2.http.Field.class)
								.addMember("value", "\"" + rpi.name + "\"").build();
						pb.addAnnotation(mbA);
						mb.addParameter(pb.build());
					}
					
				}
				
			}else if(httpInterface.httpMethod.equalsIgnoreCase("GET")) {
				
				for (int i = 0; i < httpInterface.getHttpParameters().size(); i++) {
					HttpParameter rpi = httpInterface.getHttpParameters().get(i);
					if(StringUtils.isEmpty(rpi.name)) {
						continue;
					}
					Type type =rpi.getJavaType();
					ParameterSpec.Builder pb=null;
					try {
					    pb = ParameterSpec
							.builder( String.class , rpi.name);
					}catch(Throwable e) {
						e.printStackTrace();
					}
					
					if(rpi.isPathVariable) {
						AnnotationSpec mbA = AnnotationSpec.builder(retrofit2.http.Path.class)
								.addMember("value", "\"" + rpi.name + "\"").build();
						pb.addAnnotation(mbA);
					}else {
						AnnotationSpec mbA = AnnotationSpec.builder(retrofit2.http.Query.class)
								.addMember("value", "\"" + rpi.name + "\"").build();
						pb.addAnnotation(mbA);
						
					}
					
					
					mb.addParameter(pb.build());
				}

			}
 
			if (httpInterface.httpMethod.equals("POST") && httpInterface.getHttpParameters().size() == 0) {
				ParameterSpec.Builder pb = ParameterSpec.builder(String.class, "emptyStr");
				AnnotationSpec mbA = AnnotationSpec.builder(retrofit2.http.Field.class)
						.addMember("value", "\"emptyStr\"").build();
				pb.addAnnotation(mbA);
				ParameterSpec parameterSpec = pb.build();
				mb.addParameter(parameterSpec);
			}
			Type returnType=null;
			if(void.class.equals(httpInterface.getJavaReturnType()) || Void.class.equals(httpInterface.getJavaReturnType())) {
				returnType=okhttp3.Response.class;
			}else {
				returnType=buildReturnType(invokeType,httpInterface.getJavaReturnType());
			}
			
			try {
			mb.returns(returnType);
			}catch(Throwable e) {
				e.printStackTrace();
			}
			builder.addMethod(mb.build());
		}
		return builder.build();
	}
	
	public static Type buildReturnType(String invokeType,Type javaReturnType) {
		Type returnType = new ParameterizedType() {
			@Override
			public Type getRawType() {
				return JavaCodeTypes.getReturnType(invokeType);
			}

			@Override
			public Type getOwnerType() {
				return null;
			}

			@Override
			public Type[] getActualTypeArguments() {
				return new Type[] {javaReturnType};
			}
		};
		return returnType;
	}
	
	 public static void generateJavaAPIFile(Class<?> cls,String invokeType,String baseUrl,String outPackageName,String outputDir) {
         File out = new File(outputDir);
    	 ModuleInfo moduleInfo = new ClassParser(new SourceParserContext().getCommentStore()).parseModule(cls).get();
    	 if(moduleInfo==null) {
    		 return;
    	 }
    	 
    	 if(!StringUtils.isEmpty(baseUrl)) {
    		 if(!baseUrl.startsWith("/")){
    			 baseUrl="/"+baseUrl;
        	 }
    		 moduleInfo.setUrl(baseUrl+moduleInfo.getUrl());
    	 }
		 TypeSpec typeSpec = toJavaFile(invokeType,moduleInfo);
		 JavaFile javaFile = JavaFile.builder(outPackageName, typeSpec).build();
		 try {
			 javaFile.writeTo(out);
		 }catch (Exception e) {
		     logger.error("",e);
		 }
     }
}
