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

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.security.Policy.Parameters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hngd.doc.core.FieldInfo;
import com.hngd.doc.core.InterfaceInfo;
import com.hngd.doc.core.InterfaceInfo.ParamType;
import com.hngd.doc.core.InterfaceInfo.RequestParameterInfo;
import com.hngd.doc.core.MethodInfo;
import com.hngd.doc.core.ModuleInfo;
import com.hngd.doc.core.MethodInfo.ParameterInfo;
import com.hngd.doc.core.parse.ControllerClassCommentParser;
import com.hngd.doc.core.parse.EntityClassCommentParser;
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
 * @author
 */
public class SwaggerDocGenerator {
	static List<String> application_json = Arrays.asList("application/json", "*");
	static List<String> application_url_encode = Arrays.asList("application/json");
	private static final Logger logger = LoggerFactory.getLogger(SwaggerDocGenerator.class);
	Swagger mSwagger;

	public SwaggerDocGenerator(Swagger mSwagger) {
		this.mSwagger = mSwagger;
	}

	public static List<Class<?>> getClassBelowPacakge(String packageName) {
		String packagePath = packageName.replaceAll("\\.", "/");
		Enumeration<URL> dirs = null;
		try {
			dirs = SwaggerDocGenerator.class.getClassLoader().getResources(packagePath);
		} catch (IOException e) {
			logger.error("", e);
		}
		List<Class<?>> clazzs = new LinkedList<>();
		while (dirs.hasMoreElements()) {
			URL url = dirs.nextElement();
			File file = new File(url.getFile());
			// 把此目录下的所有文件列出
			String[] classes = file.list();
			// 循环此数组，并把.class去掉
			for (String className : classes) {
				if (!className.endsWith(".class")) {
					logger.error("the file {} is not a class",file.getAbsolutePath() + className );
					continue;
				}
				className = className.substring(0, className.length() - 6);
				// 拼接上包名，变成全限定名
				String qName = packageName + "." + className;
				// 去掉Mybatis生成的Example类
				if (qName.endsWith("Example")) {
					continue;
				}
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

	public void parse(String packageName) {
		List<Class<?>> clses = getClassBelowPacakge(packageName);
		parse(clses);
	}

	public void parse(List<Class<?>> clses) {
		List<Tag> tags = new ArrayList<Tag>();
		Map<String, Path> paths = new HashMap<String, Path>();
		mSwagger.setPaths(paths);
		mSwagger.setTags(tags);
		for (Class<?> cls : clses) {
			ModuleInfo moduleInfo = processClass(cls);
			if (moduleInfo != null) {
				String classComment = ControllerClassCommentParser.classComments.get(moduleInfo.className);
				Tag classCommentTag = null;
				if (classComment != null) {
					classCommentTag = new Tag();
					classCommentTag.setName(classComment);
					tags.add(classCommentTag);
				} else {
					logger.warn("the comment for class:{} is empty",moduleInfo.className);
				}
				for (InterfaceInfo interfaceInfo : moduleInfo.interfaceInfos) {
					String methodKey = cls.getSimpleName() + "#" + interfaceInfo.methodName;
					MethodInfo methodComment = ControllerClassCommentParser.methodComments.get(methodKey);
					if (methodComment == null || methodComment.parameters == null) {
						logger.error("the method comment for method[" + methodKey + "] is empty");
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
						continue;
					}
					List<io.swagger.models.parameters.Parameter> parameters = new ArrayList<io.swagger.models.parameters.Parameter>();
					for (int i = 0; i < interfaceInfo.parameterInfos.size(); i++) {
						RequestParameterInfo rpi = interfaceInfo.parameterInfos.get(i);
						Type pt = interfaceInfo.parameterTypes.get(i);
						if (i >= methodComment.parameters.size()) {
							logger.error(moduleInfo.moduleName + "." + interfaceInfo.methodName + "." + rpi);
						}
						ParameterInfo pc = methodComment.parameters.get(i);
						Type parameterType = interfaceInfo.parameterTypes.get(i);
						if (!isPrimitiveType(parameterType)) {
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
							if (rpi.paramType.equals(ParamType.REQUEST)) {
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
							} else if (rpi.paramType.equals(ParamType.PATH)) {
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
							if (rpi.paramType.equals(ParamType.REQUEST)) {
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
							} else if (rpi.paramType.equals(ParamType.PATH)) {
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
		}
	}
    public static final List<Class<? extends Annotation>> httpInterfaceAnnotationClazzs=Arrays.asList(
    		RequestMapping.class,
    		PostMapping.class,
    		GetMapping.class,
    		DeleteMapping.class,
    		PatchMapping.class,
    		PutMapping.class
    		
    		);
    		
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
		mi.className = cls.getSimpleName();
		RequestMethod[] requestMethods = requestMapping.method();
		RequestMethod requestMethod = null;
		if (requestMethods.length > 0) {
			requestMethod = requestMethods[0];
		}
		// TODO Auto-generated method stub
		Method[] methods = cls.getDeclaredMethods();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			if(isHttpInterface(method)){
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
    
private static Optional<? extends Annotation>  getHttpRequestInfo(Method method) {
		
		return httpInterfaceAnnotationClazzs.stream()
		.filter(clazz->method.getAnnotation(clazz)!=null)
		.map(clazz->method.getAnnotation(clazz))
		 .findAny();
		  
		 

	}
	
	private static boolean isHttpInterface(Method method) {
		
	return getHttpRequestInfo(method).isPresent();
		 

	}

	private static InterfaceInfo processMethod(Method method) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		InterfaceInfo info = new InterfaceInfo();
		
		Optional<? extends Annotation> annotation = getHttpRequestInfo(method);
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
						rpi.paramType = ParamType.REQUEST;
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
						rpi.paramType = ParamType.PATH;
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

	private static boolean isPrimitiveType(Type parameterType) {
		if (!(parameterType instanceof Class<?>)) {
			return false;
		}
		if (String.class.equals(parameterType)) {
			return true;
		}
		Class<?> cls = (Class<?>) parameterType;
		if (Number.class.isAssignableFrom(cls)) {
			return true;
		}
		if (Boolean.class.isAssignableFrom(cls)) {
			return true;
		}
		return false;
	}
}
