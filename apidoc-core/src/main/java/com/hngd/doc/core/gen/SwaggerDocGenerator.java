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

import io.swagger.converter.ModelConverters;
import io.swagger.models.ArrayModel;
import io.swagger.models.Model;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.RefModel;
import io.swagger.models.Response;
import io.swagger.models.Swagger;
import io.swagger.models.Tag;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.FormParameter;
import io.swagger.models.parameters.PathParameter;
import io.swagger.models.parameters.QueryParameter;
import io.swagger.models.properties.AbstractProperty;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;

/**
 * @author tqd
 */
public class SwaggerDocGenerator {
	static List<String> application_json = Arrays.asList("application/json", "*");
	static List<String> application_url_encode = Arrays.asList("application/json");
	private static final Logger logger = LoggerFactory.getLogger(SwaggerDocGenerator.class);
	Swagger mSwagger;

	public SwaggerDocGenerator(Swagger mSwagger) {
		this.mSwagger = mSwagger;
	}

	
   
	public void parse(String packageName) {
		List<Class<?>> clses = ClassUtils.getClassBelowPacakge(packageName);
		parse(clses);
	}

	public void parse(List<Class<?>> clses) {
		List<Tag> tags = new ArrayList<Tag>();
		Map<String, Path> paths = new HashMap<String, Path>();
		mSwagger.setPaths(paths);
		mSwagger.setTags(tags);
		clses.stream()
		  .map(clazz->processClass(clazz))
		  .filter(mi->mi!=null)
		  .forEach(mi->{
			  module2doc(mi,paths,tags);
		  });
	}
	
	private void module2doc(ModuleInfo moduleInfo,Map<String, Path> paths,List<Tag> tags) {
		
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
			Path path = new Path();
			Operation op = new Operation();
			List<String> operationTags = new ArrayList<>();
			if (methodComment.createTimeStr != null) {
				operationTags.add(methodComment.createTimeStr);
			}
			if (classCommentTag != null) {
				operationTags.add(classCommentTag.getName());
			}
			op.setTags(operationTags);
			op.setConsumes(CollectionUtils.isEmpty(interfaceInfo.consumes) ? application_url_encode : interfaceInfo.consumes);
			op.setProduces(CollectionUtils.isEmpty(interfaceInfo.produces) ? application_json : interfaceInfo.produces);
			op.setOperationId(interfaceInfo.methodName);
			if (methodComment != null) {
				op.setDescription(methodComment.comment);
			}
			if (interfaceInfo.parameterInfos.size() > methodComment.parameters.size()) {
				logger.warn("the interface[{}]  parameter size is not equal to related method parameter size",methodKey);
				continue;
			}
			List<io.swagger.models.parameters.Parameter> parameters = new ArrayList<>();
			for (int i = 0; i < interfaceInfo.parameterInfos.size(); i++) {
				RequestParameterInfo rpi = interfaceInfo.parameterInfos.get(i);
				Type pt = interfaceInfo.parameterTypes.get(i);
				if (i >= methodComment.parameters.size()) {
					logger.error(moduleInfo.moduleName + "." + interfaceInfo.methodName + "." + rpi);
				}
				ParameterInfo pc = methodComment.parameters.get(i);
				Type parameterType = interfaceInfo.parameterTypes.get(i);
				if (!TypeUtils.isPrimitiveType(parameterType)) {
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
									} else if (Number.class.isAssignableFrom(argumentClass)) {
										pc.format = "Number";
										pc.type = argumentClass.getSimpleName();
										pc.isArgumentTypePrimitive = true;
									} else if (Boolean.class.isAssignableFrom(argumentClass)) {
										pc.format = "Boolean";
										pc.type = argumentClass.getSimpleName();
										pc.isArgumentTypePrimitive = true;
									} else {
										pc.format = "Object";
										pc.type = argumentClass.getSimpleName();
										pc.isArgumentTypePrimitive = false;
									}
								}
							}
						} else {
						}
					}
				} else {
					Class<?> argumentClass = (Class<?>) parameterType;
					if (String.class.isAssignableFrom(argumentClass)) {
						pc.format = "string";
						pc.type = "string";
						pc.isArgumentTypePrimitive = true;
					} else if (Number.class.isAssignableFrom(argumentClass)) {
						pc.type = "number";
						pc.format = argumentClass.getSimpleName().toLowerCase();
						pc.isArgumentTypePrimitive = true;
					} else if (Boolean.class.isAssignableFrom(argumentClass)) {
						pc.type = "boolean";
						pc.format = argumentClass.getSimpleName().toLowerCase();
						pc.isArgumentTypePrimitive = true;
					} else {
						pc.type = "object";
						pc.format = argumentClass.getSimpleName();
						pc.isArgumentTypePrimitive = false;
					}
				}
				resolveType(parameterType, mSwagger);
				if (interfaceInfo.requestType.equals(RequestMethod.GET.name())) {
					if (rpi.paramType.equals(HttpRequestParamType.REQUEST)) {
						if (pc.ref != null) {
							BodyParameter bp = new BodyParameter();
							bp.setDescription(pc.comment);
							bp.setIn("query");
							bp.setName(rpi.name);
							bp.setRequired(rpi.required);
							Model schema = null;
							if (pc.isCollection) {
								ArrayModel am = new ArrayModel();
								am.setType("array");
								AbstractProperty items = new RefProperty();
								if (pc.isArgumentTypePrimitive) {
									items = new ArrayProperty();
									items.setFormat(pc.format);
								} else {
									((RefProperty) items).set$ref("#/definitions/" + pc.type);
									// am.setReference("#/definitions/"+pc.type);
								}
								items.setType(pc.type);
								am.setItems(items);
								schema = am;
							} else {
								schema = new RefModel("#/definitions/" + pc.ref);
							}
							bp.setSchema(schema);
							parameters.add(bp);
						} else {
							QueryParameter param = new QueryParameter();
							param.setIn("query");
							param.setName(rpi.name);
							param.setRequired(rpi.required);
							param.setType(pc.type);
							param.setFormat(pc.format);
							param.setDescription(pc.comment);
							parameters.add(param);
						}
					} else if (rpi.paramType.equals(HttpRequestParamType.PATH)) {
						PathParameter pathParameter = new PathParameter();
						pathParameter.setIn("path");
						pathParameter.setName(rpi.name);
						pathParameter.setRequired(rpi.required);
						pathParameter.setType(pc.type);
						pathParameter.setFormat(pc.format);
						pathParameter.setDescription(pc.comment);
						parameters.add(pathParameter);
					}
				} else {
					if (rpi.paramType.equals(HttpRequestParamType.REQUEST)) {
						if (pc.ref != null) {
							BodyParameter bp = new BodyParameter();
							bp.setDescription(pc.comment);
							bp.setIn("body");
							bp.setName(rpi.name);
							bp.setRequired(rpi.required);
							Model schema = null;
							if (pc.isCollection) {
								ArrayModel am = new ArrayModel();
								am.setType("array");
								AbstractProperty items = new RefProperty();
								if (pc.isArgumentTypePrimitive) {
									items = new ArrayProperty();
									items.setFormat(pc.format);
								} else {
									((RefProperty) items).set$ref("#/definitions/" + pc.type);
									// am.setReference("#/definitions/"+pc.type);
								}
								items.setType(pc.type);
								items.setFormat(pc.format);
								am.setItems(items);
								schema = am;
							} else {
								schema = new RefModel("#/definitions/" + pc.ref);
							}
							bp.setSchema(schema);
							parameters.add(bp);
						} else {
							FormParameter param = new FormParameter();
							param.setIn("body");
							param.setName(rpi.name);
							param.setRequired(rpi.required);
							param.setType(pc.type);
							param.setDescription(pc.comment);
							param.setFormat(pc.format);
							parameters.add(param);
						}
					} else if (rpi.paramType.equals(HttpRequestParamType.PATH)) {
						PathParameter pathParameter = new PathParameter();
						pathParameter.setIn("path");
						pathParameter.setName(rpi.name);
						pathParameter.setRequired(rpi.required);
						pathParameter.setType(pc.type);
						pathParameter.setDescription(pc.comment);
						pathParameter.setFormat(pc.format);
						parameters.add(pathParameter);
					}
				}
			}
			op.setParameters(parameters);
			Map<String, Response> responses = new HashMap<String, Response>();
			Response resp = new Response();
			if (methodComment != null) {
				resp.setDescription(methodComment.retComment);
			}
			String firstKey = resolveType(interfaceInfo.retureType, mSwagger);
			RefProperty schema = new RefProperty();
			schema.set$ref("#/definitions/" + firstKey);
			resp.setSchema(schema);
			resp.setDescription("test");
			responses.put("200", resp);
			op.setResponses(responses);
			path.set(interfaceInfo.requestType.toLowerCase(), op);
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

	public static String resolveType(Type type, Swagger swagger) {
		Map<String, Model> models = ModelConverters.getInstance().read(type);
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
			Model model = models.get(key);
			Map<String, Property> properties = new HashMap<String, Property>();
			Map<String, Property> cps = model.getProperties();
			if (cps != null) {
				model.getProperties().values().forEach(property -> {
					String name = property.getName();
					if (name.startsWith("get")) {
						name = name.replace("get", "");
					}
					String fcKey = null;
					if (type instanceof Class<?>) {
						String typeName = ((Class<?>) type).getSimpleName();
						fcKey = typeName + "#" + name;
					} else if (type instanceof ParameterizedType) {
						ParameterizedType pt = (ParameterizedType) type;
						// fcKey=name;
						String typeName = ((Class<?>) pt.getRawType()).getSimpleName();
						fcKey = typeName + "#" + name;
					}
					FieldInfo fi = EntityClassCommentParser.fieldComments.get(fcKey);
					if (fi != null) {
						property.setDescription(fi.comment);
					}
					property.setName(name);
					properties.put(name, property);
				});
				model.getProperties().clear();
				model.setProperties(properties);
				swagger.addDefinition(key, model);
			}
		}
		return firstKey;
	}


}
