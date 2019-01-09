/**
 * Copyright (c) 2017,湖南华南光电科技股份有限公司
 * All rights reserved.
 *
 * @文件名：SwaggerDocGenerator.java
 * @时间：2017年1月9日 下午2:36:00
 * @作者：
 * @备注：
 * @版本:
 */

package com.hngd.doc.core.gen;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hngd.doc.core.FieldInfo;
import com.hngd.doc.core.InterfaceInfo;
import com.hngd.doc.core.InterfaceInfo.HttpRequestParamType;
import com.hngd.doc.core.InterfaceInfo.RequestParameterInfo;
import com.hngd.doc.core.MethodInfo;
import com.hngd.doc.core.ModuleInfo;
import com.hngd.doc.core.MethodInfo.ParameterInfo;
import com.hngd.doc.core.parse.ControllerClassCommentParser;
import com.hngd.doc.core.parse.EntityClassCommentParser;
import com.hngd.doc.core.util.ClassUtils;
import com.hngd.doc.core.util.RestClassUtils;
import com.hngd.doc.core.util.TypeNameUtils;
import com.hngd.doc.core.util.TypeUtils;

import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.jackson.SwaggerModule;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.BooleanSchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.FileSchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.NumberSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.PathParameter;
import io.swagger.v3.oas.models.parameters.QueryParameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.tags.Tag;

 

/**
 * @author tqd
 */
public class SwaggerDocGenerator {
	static List<String> application_json = Arrays.asList("application/json", "*");
	static List<String> application_url_encode = Arrays.asList("application/json");
	private static final Logger logger = LoggerFactory.getLogger(SwaggerDocGenerator.class);
	public OpenAPI openAPI;

	public SwaggerDocGenerator(OpenAPI openAPI) {
		this.openAPI = openAPI;
	}

	
   
	public  void parse(String packageName) {
		List<Class<?>> clses = ClassUtils.getClassBelowPacakge(packageName);
		parse(clses);
	}

	public void parse(List<Class<?>> clses) {
		List<Tag> tags = new ArrayList<Tag>();
		Paths paths=new Paths();
		openAPI.setPaths(paths);
		openAPI.setTags(tags);
		clses.stream()
		  .map(clazz->processClass(clazz))
		  .filter(mi->mi!=null)
		  .forEach(mi->{
			  module2doc(mi,paths,tags);
		  });
	}
	
	private void module2doc(ModuleInfo moduleInfo,Paths paths,List<Tag> tags) {
		
		String classComment = ControllerClassCommentParser.classComments.get(moduleInfo.simpleClassName);
		Tag classCommentTag = null;
		if (classComment != null) {
			classCommentTag = new Tag();
			classCommentTag.setName(classComment);
			tags.add(classCommentTag);
		} else {
			logger.warn("the comment for class:{} is empty",moduleInfo.canonicalClassName);
		}
		for (InterfaceInfo interfaceInfo : moduleInfo.interfaceInfos) {
			String methodKey = moduleInfo.simpleClassName + "#" + interfaceInfo.methodName;
			MethodInfo methodComment = ControllerClassCommentParser.methodComments.get(methodKey);
			if (methodComment == null || methodComment.parameters == null) {
				logger.warn("the method comment for method[{}] is empty",methodKey);
				continue;
			}
			String pathStr = moduleInfo.moduleUrl + interfaceInfo.methodUrl;
			PathItem path = new PathItem();
			Operation op = new Operation();
			List<String> operationTags = new ArrayList<>();
			if (methodComment!=null && methodComment.createTimeStr != null) {
				operationTags.add(methodComment.createTimeStr);
			}
			if (classCommentTag != null) {
				operationTags.add(classCommentTag.getName());
			}
			op.setTags(operationTags);
			//op.set
			//op.setConsumes(CollectionUtils.isEmpty(interfaceInfo.consumes) ? application_url_encode : interfaceInfo.consumes);
			//op.setProduces(CollectionUtils.isEmpty(interfaceInfo.produces) ? application_json : interfaceInfo.produces);
			
			op.setOperationId(interfaceInfo.methodName);
			if (methodComment != null) {
				op.setDescription(methodComment.comment);
			}
			if (methodComment!=null && interfaceInfo.parameterInfos.size() > methodComment.parameters.size()) {
				logger.warn("the interface[{}]  parameter size is not equal to related method parameter size",methodKey);
				//continue;
			}
			List<io.swagger.v3.oas.models.parameters.Parameter> parameters = new ArrayList<>();
			Content content=new Content();
			for (int i = 0; i < interfaceInfo.parameterInfos.size(); i++) {
				RequestParameterInfo rpi = interfaceInfo.parameterInfos.get(i);
				Type pt = interfaceInfo.parameterTypes.get(i);
				if (i >= methodComment.parameters.size()) {
					logger.error(moduleInfo.moduleName + "." + interfaceInfo.methodName + "." + rpi);
				}
				ParameterInfo pc = methodComment.parameters.get(i);
				Type parameterType = interfaceInfo.parameterTypes.get(i);
		        pc.ref = TypeNameUtils.getTypeName(parameterType);
				if (pc.ref.contains("<")) {
				    pc.ref = pc.ref.replace("<", "").replace(">", "");
				}
					if (parameterType instanceof ParameterizedType) {
						ParameterizedType ppt = (ParameterizedType) parameterType;
						Type rawType = ppt.getRawType();
						if (rawType instanceof Class<?>) {
							Class<?> rawClass = (Class<?>) rawType;
							if (Collection.class.isAssignableFrom(rawClass)) {
								pc.isCollection = true;
								Type[] argumentTypes = ppt.getActualTypeArguments();
								if (argumentTypes != null && argumentTypes.length > 0) {
									Type argumentType0 = argumentTypes[0];
									Class<?> argumentClass = (Class<?>) argumentType0;
									if (String.class.isAssignableFrom(argumentClass)) {
										pc.format = "string";
										pc.type = "string";
										pc.isArgumentTypePrimitive = true;
										pc.schema=new StringSchema();
									} else if (Number.class.isAssignableFrom(argumentClass)) {
										pc.format = "Number";
										pc.type = argumentClass.getSimpleName();
										pc.isArgumentTypePrimitive = true;
										pc.schema=new NumberSchema();
									} else if (Boolean.class.isAssignableFrom(argumentClass)) {
										pc.format = "Boolean";
										pc.type = argumentClass.getSimpleName();
										pc.isArgumentTypePrimitive = true;
										pc.schema=new BooleanSchema();
									}else if(MultipartFile.class.isAssignableFrom(argumentClass)) {
										pc.format = "File";
										pc.type = argumentClass.getSimpleName();
										pc.isArgumentTypePrimitive = true;
										pc.schema=new FileSchema();
									}
									else {
										pc.format = "Object";
										pc.type = argumentClass.getSimpleName();
										pc.isArgumentTypePrimitive = false;
										pc.schema=new ObjectSchema();
										//pc.schema.setType(pc.type);
									}
								}
							}
						} else {
							logger.error("aha");
						}
					 
				} else {
					Class<?> argumentClass = (Class<?>) parameterType;
					if (String.class.isAssignableFrom(argumentClass)) {
						pc.format = "string";
						pc.type = "string";
						pc.isArgumentTypePrimitive = true;
						pc.schema=new StringSchema();
					} else if (Number.class.isAssignableFrom(argumentClass)) {
						pc.type = "number";
						pc.format = argumentClass.getSimpleName().toLowerCase();
						pc.isArgumentTypePrimitive = true;
						pc.schema=new NumberSchema();
					} else if (Boolean.class.isAssignableFrom(argumentClass)) {
						pc.type = "boolean";
						pc.format = argumentClass.getSimpleName().toLowerCase();
						pc.isArgumentTypePrimitive = true;
						pc.schema=new BooleanSchema();
					} else if(MultipartFile.class.isAssignableFrom(argumentClass)) {
						pc.format = "File";
						pc.type = argumentClass.getSimpleName();
						pc.isArgumentTypePrimitive = true;
						pc.schema=new FileSchema();
					}else {
						pc.type = "object";
						pc.format = argumentClass.getSimpleName();
						pc.isArgumentTypePrimitive = false;
						pc.schema=new ObjectSchema();
					}
				}
				resolveType(parameterType, openAPI);
				if (interfaceInfo.requestType.equals(RequestMethod.GET.name())) {
					if (rpi.paramType.equals(HttpRequestParamType.REQUEST)) {
						if (pc.ref != null) {
							QueryParameter bp = new QueryParameter();
							bp.setDescription(pc.comment);
							bp.setName(rpi.name);
							bp.setRequired(rpi.required);
					        bp.set$ref("#/components/schemas/" + pc.ref);
							parameters.add(bp);
						} else {
							QueryParameter param = new QueryParameter();
							param.setIn("query");
							param.setName(rpi.name);
							param.setRequired(rpi.required);
							param.setSchema(pc.schema);
							//param.setType(pc.type);
							//param.setFormat(pc.format);
							param.setDescription(pc.comment);
							parameters.add(param);
						}
					} else if (rpi.paramType.equals(HttpRequestParamType.PATH)) {
						PathParameter pathParameter = new PathParameter();
						pathParameter.setIn("path");
						pathParameter.setName(rpi.name);
						pathParameter.setRequired(rpi.required);
						pathParameter.setSchema(pc.schema);
						//pathParameter.setType(pc.type);
						//pathParameter.setFormat(pc.format);
						pathParameter.setDescription(pc.comment);
						parameters.add(pathParameter);
					}
				} else {
					//POST
					
					MediaType item=new MediaType();
					if (rpi.paramType.equals(HttpRequestParamType.REQUEST)) {
						if (pc.ref != null) {
							item.setSchema(pc.schema);
							pc.schema.$ref("#/components/schemas/" + pc.ref);
						} else {
							item.setSchema(pc.schema);
						}
					} else if (rpi.paramType.equals(HttpRequestParamType.PATH)) {
						PathParameter pathParameter = new PathParameter();
						pathParameter.setIn("path");
						pathParameter.setName(rpi.name);
						pathParameter.setRequired(rpi.required);
						//pathParameter.setType(pc.type);
						pathParameter.setDescription(pc.comment);
						pathParameter.setSchema(pc.schema);
						//pathParameter.setFormat(pc.format);
						parameters.add(pathParameter);
					}
					
					
					content.addMediaType(rpi.name, item);
				}
			}
			RequestBody requestBody=new RequestBody();
			requestBody.setContent(content);
 
			op.setRequestBody(requestBody);
			op.setParameters(parameters);
			ApiResponses responses = new ApiResponses();
			ApiResponse resp = new ApiResponse();
			if (methodComment != null) {
				resp.setDescription(methodComment.retComment);
			}
			String firstKey = resolveType(interfaceInfo.retureType, openAPI);
		    Schema schema=new ObjectSchema();
			schema.set$ref("#/components/schemas/" + firstKey);
			//resp.set$ref("#/components/schemas/" + firstKey);
			Content respContent=new Content();
			MediaType mt=new MediaType();
			mt.setSchema(schema);
			
			respContent.put("text/plain", mt);
			resp.setContent(respContent);
			 
			resp.setDescription("test");
			
			responses.put("200", resp);
			op.setResponses(responses);
			if(interfaceInfo.requestType.equals(RequestMethod.GET.name())) {
				path.setGet(op);
			}else {
				path.setPost(op);
			}
			paths.put(pathStr, path);
	    }
	}

    		
	public static ModuleInfo processClass(Class<?> cls) {
		ModuleInfo mi = new ModuleInfo();
		Controller controller = cls.getAnnotation(Controller.class);
		RestController restController = cls.getAnnotation(RestController.class);
		if (controller == null && restController == null) {
			 
			logger.error("class:{} has no annotation {},{}",cls.getName(),Controller.class.getName(),RestController.class.getName());
			return null;
		}
		RequestMapping requestMapping = cls.getAnnotation(RequestMapping.class);
		if (requestMapping == null || requestMapping.value().length <= 0) {
			logger.warn("class[" + cls.getName() + "] has no annotation[" + RequestMapping.class.getName() + "]");
			return null;
		}
		mi.moduleUrl = requestMapping.value()[0];
		if (!StringUtils.startsWith(mi.moduleUrl, "/")) {
			mi.moduleUrl = "/" + mi.moduleUrl;
		}
		mi.moduleName = mi.moduleUrl.substring(1);
		mi.simpleClassName = cls.getSimpleName();
		RequestMethod[] requestMethods = requestMapping.method();
		RequestMethod requestMethod = null;
		if (requestMethods.length > 0) {
			requestMethod = requestMethods[0];
		}
		// TODO Auto-generated method stub
		Method[] methods = cls.getDeclaredMethods();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			if(RestClassUtils.isHttpInterface(method)){
				InterfaceInfo info=null;
				try {
					info = processMethod(method);
				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					logger.error("", e);
				}
				if (info != null) {
					mi.interfaceInfos.add(info);
				}
			}else{
				
			 
				logger.warn("method:{} has no http request annotation",method.getName());
			}
		}
		return mi;
	}
    


	private static InterfaceInfo processMethod(Method method) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		InterfaceInfo info = new InterfaceInfo();
		
		Optional<? extends Annotation> annotation = RestClassUtils.getHttpRequestInfo(method);
		Annotation a=annotation.get();
        Method consumesMethod=a.getClass().getDeclaredMethod("consumes");
		String[] consumes = (String[]) consumesMethod.invoke(a);
		if (consumes != null && consumes.length > 0) {
			info.consumes = new ArrayList<>(Arrays.asList(consumes));
		}
		
		 Method producesMethod=a.getClass().getDeclaredMethod("produces");
		String[] produces = (String[]) producesMethod.invoke(a);
		if (produces != null && produces.length > 0) {
			info.produces = new ArrayList<>(Arrays.asList(produces));
		}
		Method valueMethod=a.getClass().getDeclaredMethod("value");
		Object value=valueMethod.invoke(a);
		if(value!=null){
			info.methodUrl =((String[])value)[0];
		}else{
			Method pathMethod=a.getClass().getDeclaredMethod("path");
			String[] path=(String[]) pathMethod.invoke(a);
			info.methodUrl =path[0];
		}
		 
		if (!StringUtils.startsWith(info.methodUrl, "/")) {
			info.methodUrl = "/" + info.methodUrl;
		}
		
		if(a instanceof RequestMapping){
			info.requestType = ((RequestMapping)a).method()[0].name();
		}else{
			info.requestType = a.annotationType().getSimpleName().replace("Mapping", "");
		}
		
		info.retureTypeName = method.getReturnType().getSimpleName();
		info.retureType = method.getReturnType();
		info.methodName = method.getName();
		info.retureType = method.getGenericReturnType();
		
		Annotation[][] annotationss = method.getParameterAnnotations();
		Parameter[] parameters = method.getParameters();
		Class<?>[] parameterTypes = method.getParameterTypes();
		for (int i = 0; i < parameters.length; i++) {
			// System.out.println(parameterTypes[i].getName());
			Parameter parameter = parameters[i];
			Annotation[] annotations = parameter.getAnnotations();
			if (annotations.length > 0) {
				for (Annotation a1 : annotations) {
					if (a1 instanceof RequestParam) {
						RequestParam requestParam = (RequestParam) a1;
						RequestParameterInfo rpi = new RequestParameterInfo();
						rpi.name = requestParam.value();
						rpi.paramType = HttpRequestParamType.REQUEST;
						rpi.required = requestParam.required();
						info.parameterInfos.add(rpi);
						info.parameterTypes.add(parameter.getParameterizedType());
						if (!info.isMultipart) {
							info.isMultipart = TypeUtils.isMultipartType(parameter.getParameterizedType());
						}
						break;
					} else if (a1 instanceof PathVariable) {
						PathVariable pa = (PathVariable) a1;
						RequestParameterInfo rpi = new RequestParameterInfo();
						rpi.name = pa.value();
						rpi.paramType = HttpRequestParamType.PATH;
						rpi.required = true;
						info.parameterInfos.add(rpi);
						if (!info.isMultipart) {
							info.isMultipart = TypeUtils.isMultipartType(parameter.getParameterizedType());
						}
						info.parameterTypes.add(parameter.getParameterizedType());
						break;
					}
				}
			}
		}
		return info;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String resolveType(Type type, OpenAPI swagger) {
		Map<String, Schema> models = ModelConverters.getInstance().read(type);
		if (type instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) type;
			Type[] subTypes = pt.getActualTypeArguments();
			for (Type subType : subTypes) {
				resolveType(subType, swagger);
			}
		}
		String firstKey = null;
		for (String key : models.keySet()) {
			firstKey = key;
			Schema model = models.get(key);
			Map<String, Schema> properties = new HashMap<>();
			@SuppressWarnings("unchecked")
			Map<String, Schema> cps = model.getProperties();
			if (cps == null) {
				continue;
			}

			model.getProperties().values().forEach(property -> {

				Schema ss = (Schema) property;

				String name = ss.getName();
				if (name.startsWith("get")) {
					name = name.replace("get", "");
				}
                String propertyComment=getPropertyComment(type,name);
				if (propertyComment != null) {
					ss.setDescription(propertyComment);
				}
				ss.setName(name);
				properties.put(name, ss);
			});
			model.getProperties().clear();
			model.setProperties(properties);
			swagger.schema(key, model);
		}

		return firstKey;
	}
	
	private static String getPropertyComment(Type type,String propertyName) {
		
		String fcKey = null;
		if (type instanceof Class<?>) {
			String typeName = ((Class<?>) type).getSimpleName();
			fcKey = typeName + "#" + propertyName;
		} else if (type instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) type;
			// fcKey=name;
			String typeName = ((Class<?>) pt.getRawType()).getSimpleName();
			fcKey = typeName + "#" + propertyName;
		}
		FieldInfo fi = EntityClassCommentParser.fieldComments.get(fcKey);
		if(fi!=null) {
			return fi.comment;
		}else {
			return null;
		}
	}


}
