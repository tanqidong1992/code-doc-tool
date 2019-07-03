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

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import com.hngd.api.http.HttpInterfaceInfo;
import com.hngd.api.http.HttpParameterInfo;
import com.hngd.api.http.HttpParameterInfo.HttpParameterType;
import com.hngd.constant.Constants;
import com.hngd.doc.core.FieldInfo;
import com.hngd.doc.core.MethodInfo;
import com.hngd.doc.core.ModuleInfo;
import com.hngd.doc.core.ParameterInfo;
import com.hngd.doc.core.parse.CommonClassCommentParser;
import com.hngd.doc.core.util.ClassUtils;
import com.hngd.doc.core.util.TypeNameUtils;
import com.hngd.doc.core.util.TypeUtils;

import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.BooleanSchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.DateSchema;
import io.swagger.v3.oas.models.media.Encoding;
import io.swagger.v3.oas.models.media.FileSchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.NumberSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.tags.Tag;

/**
 * @author tqd
 */
public class OpenAPITool {
	static List<String> application_json = Arrays.asList("application/json", "*");
	private static final Logger logger = LoggerFactory.getLogger(OpenAPITool.class);
	public OpenAPI openAPI;

	public OpenAPITool(OpenAPI openAPI) {
		this.openAPI = openAPI;
	}

	public void parse(String packageName) {
		List<Class<?>> clses = ClassUtils.getClassBelowPacakge(packageName);
		parse(clses);
	}

	public void parse(List<Class<?>> clses) {
		List<Tag> tags = new ArrayList<Tag>();
		Paths paths = new Paths();
		openAPI.setPaths(paths);
		openAPI.setTags(tags);
		clses.stream().map(clazz -> ClassParser.processClass(clazz))
		    .filter(mi -> mi != null)
			.forEach(mi -> module2doc(mi, paths, tags));
	}

	private void module2doc(ModuleInfo moduleInfo, Paths paths, List<Tag> tags) {
		String classComment = CommonClassCommentParser.classComments.get(moduleInfo.simpleClassName);
		String tagName = classComment != null ? classComment : moduleInfo.getSimpleClassName();
		Tag tag = new Tag();
		tag.setName(tagName);
		if (classComment == null) {
			logger.warn("the comment for class:{} is empty", moduleInfo.canonicalClassName);
		}
		tags.add(tag);
		for (HttpInterfaceInfo interfaceInfo : moduleInfo.interfaceInfos) {
			PathItem pathItem = interface2doc(moduleInfo, interfaceInfo, tag);
			if (pathItem != null) {
				String pathKey = moduleInfo.moduleUrl + interfaceInfo.methodUrl;
				paths.put(pathKey, pathItem);
			}
		}
	}

	public static Parameter createParameter(HttpParameterInfo pc, String comment) {
		if (pc.paramType.isParameter()) {
			Parameter param = null;
			try {
				param = (Parameter) pc.paramType.getParamClass().newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				logger.error("", e);
			}
			param.setName(pc.name);
			param.setRequired(pc.required);
			param.setSchema(pc.schema);
			param.setDescription(comment);
			return param;
		} else {
			return null;
		}
	}

	private PathItem interface2doc(ModuleInfo moduleInfo, HttpInterfaceInfo interfaceInfo, Tag tag) {

		String methodKey = moduleInfo.simpleClassName + "#" + interfaceInfo.methodName;
		MethodInfo methodComment = CommonClassCommentParser.methodComments.get(methodKey);
		if (methodComment == null || methodComment.parameters == null) {
			logger.warn("the method comment for method:{} is empty", methodKey);
			return null;
		}

		PathItem path = new PathItem();
		Operation op = new Operation();
		List<String> operationTags = new ArrayList<>();
		if (methodComment.createTimeStr != null) {
			operationTags.add(methodComment.createTimeStr);
		}
		operationTags.add(tag.getName());
		op.setTags(operationTags);
		op.setOperationId(interfaceInfo.methodName);
		op.setDescription(methodComment.comment);

		List<Parameter> parameters = new ArrayList<>();

		if (!mayRequestBody(interfaceInfo.httpMethod)) {
			for (int i = 0; i < interfaceInfo.parameterInfos.size(); i++) {
				HttpParameterInfo pc = interfaceInfo.parameterInfos.get(i);
				String parameterComment = null;
				if (i < methodComment.parameters.size()) {
					ParameterInfo pi = methodComment.parameters.get(i);
					parameterComment = pi.comment;
				}
				Type parameterType = pc.getParamJavaType();
				resolveParameterInfo(pc, parameterType);
				if (!pc.isArgumentTypePrimitive) {
					resolveType(parameterType, openAPI);
				}
				Parameter param = createParameter(pc, parameterComment);
				parameters.add(param);
			}

		} else{
			MediaType item = new MediaType();
			Content content = new Content();
			if (interfaceInfo.hasRequestBody) {
				content.addMediaType(Constants.APPLICATION_JSON_VALUE, item);
				HttpParameterInfo pc = interfaceInfo.getParameterInfos().get(0);
				Type parameterType = pc.getParamJavaType();
				resolveParameterInfo(pc, parameterType);
				String key = resolveType(parameterType, openAPI);
				Schema<?> schema = openAPI.getComponents().getSchemas().get(key);
				item.setSchema(schema);
			} else {
				if (interfaceInfo.isMultipart()) {
					content.addMediaType(Constants.MULTIPART_FORM_DATA, item);
				} else {
					content.addMediaType(Constants.APPLICATION_FORM_URLENCODED_VALUE, item);
				}
				ObjectSchema schema = new ObjectSchema();
				item.setSchema(schema);
				Map<String, Encoding> encodings = new HashMap<>();
				item.setEncoding(encodings);
				for (int i = 0; i < interfaceInfo.parameterInfos.size(); i++) {
					HttpParameterInfo pc = interfaceInfo.parameterInfos.get(i);
					String parameterComment = null;
					if (i < methodComment.parameters.size()) {
						ParameterInfo pi = methodComment.parameters.get(i);
						parameterComment = pi.comment;
					}
					Type parameterType = pc.getParamJavaType();
					resolveParameterInfo(pc, parameterType);
					if (!pc.isArgumentTypePrimitive) {
						resolveType(parameterType, openAPI);
					}
					Encoding encoding = new Encoding();
					if (parameterType instanceof Class) {
						if (BeanUtils.isSimpleProperty((Class<?>) parameterType)) {
							if (parameterType.equals(String.class)) {
								encoding.setContentType("string");
							} else {
								encoding.setContentType("text/plain");
							}
						} else {
							if (TypeUtils.isMultipartType(parameterType)) {
								encoding.setContentType("application/octet-stream");
							} else {
								encoding.setContentType("application/json");
							}
						}
					} else {
						if (TypeUtils.isMultipartType(parameterType)) {
							encoding.setContentType("application/octet-stream");
						} else {
							encoding.setContentType("application/json");
						}
					}
					encodings.put(pc.name, encoding);
					if (pc.paramType.equals(HttpParameterType.query)) {
						// item.setSchema(pc.schema);
						Schema<?> propertiesItem = new Schema<>();
						propertiesItem.setDescription(parameterComment);
						propertiesItem.setType(pc.type);
						propertiesItem.set$ref(pc.ref);
						if (pc.ref != null && pc.schema instanceof ObjectSchema) {
							@SuppressWarnings("rawtypes")
							Map<String, Schema> properties = new HashMap<>();
							properties.put(pc.ref, pc.schema);
							propertiesItem.setProperties(properties);
						}
						if (pc.isRequired()) {
							schema.addRequiredItem(pc.name);
						}
						if (TypeUtils.isMultipartType(parameterType)) {
							propertiesItem.format("binary");
							propertiesItem.setType("string");
							 Class<?> type=(Class<?>) parameterType;
							 if(type.isArray()) {
								 ArraySchema as=new ArraySchema();
								 as.setDescription(parameterComment);
								 as.setType("array");
								 Schema<?> items=new Schema<>();
								 items.setType("string");
								 items.setFormat("binary");
								 as.setItems(items);
								 propertiesItem=as;
							 }
						} else {
							propertiesItem.format(pc.format);
						}
						schema.addProperties(pc.name, propertiesItem);
					} else if (pc.paramType.equals(HttpParameterType.path)) {
						Parameter pathParameter = createParameter(pc, parameterComment);
						parameters.add(pathParameter);
					}
				}
				RequestBody requestBody = new RequestBody();
				requestBody.setContent(content);
				op.setRequestBody(requestBody);
			}
		}
		op.deprecated(interfaceInfo.deprecated);
		op.setParameters(parameters);
		resolveResponse(op, interfaceInfo, methodComment != null ? methodComment.retComment : null);
		String httpMethod=interfaceInfo.httpMethod;
		addOpToPath(op,path,httpMethod);
		return path;

	}

	public static void addOpToPath(Operation op, PathItem path, String httpMethod) {
		// GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE
		if (httpMethod.equalsIgnoreCase(RequestMethod.GET.name())) {
			path.setGet(op);
		} else if (httpMethod.equalsIgnoreCase(RequestMethod.HEAD.name())) {
			path.setHead(op);
		} else if (httpMethod.equalsIgnoreCase(RequestMethod.POST.name())) {
			path.setPost(op);
		} else if (httpMethod.equalsIgnoreCase(RequestMethod.PUT.name())) {
			path.setPut(op);
		} else if (httpMethod.equalsIgnoreCase(RequestMethod.PATCH.name())) {
			path.setPatch(op);
		} else if (httpMethod.equalsIgnoreCase(RequestMethod.DELETE.name())) {
			path.setDelete(op);
		} else if (httpMethod.equalsIgnoreCase(RequestMethod.OPTIONS.name())) {
			path.setOptions(op);
		} else if (httpMethod.equalsIgnoreCase(RequestMethod.TRACE.name())) {
			path.setTrace(op);
		}

	}

	private void resolveResponse(Operation op, HttpInterfaceInfo interfaceInfo, String respComment) {
		ApiResponses responses = new ApiResponses();
		ApiResponse resp = new ApiResponse();
		String firstKey = resolveType(interfaceInfo.retureType, openAPI);
		Schema<?> schema = new ObjectSchema();
		schema.set$ref("#/components/schemas/" + firstKey);
		Content respContent = new Content();
		MediaType mt = new MediaType();
		mt.setSchema(schema);
		respContent.put(Constants.APPLICATION_JSON_VALUE, mt);
		resp.setContent(respContent);
		resp.setDescription("test");
		responses.put("200", resp);
		op.setResponses(responses);
	}

	private void resolveParameterInfo(HttpParameterInfo pc, Type parameterType) {
		Class<?> argumentClass = null;
		if (parameterType instanceof ParameterizedType) {
			ParameterizedType ppt = (ParameterizedType) parameterType;
			Type rawType = ppt.getRawType();
			if (rawType instanceof Class<?>) {
				Class<?> rawClass = (Class<?>) rawType;
				if (Collection.class.isAssignableFrom(rawClass)) {
					pc.isCollection = true;
				}
			}
			argumentClass = (Class<?>) ppt.getActualTypeArguments()[0];
		} else {
			argumentClass = (Class<?>) parameterType;
		}
		if (String.class.isAssignableFrom(argumentClass)) {
			pc.format = "string";
			pc.type = "string";
			pc.isArgumentTypePrimitive = true;
			pc.schema = new StringSchema();
		} else if (Number.class.isAssignableFrom(argumentClass)) {
			pc.type = "number";
			pc.format = argumentClass.getSimpleName().toLowerCase();
			pc.isArgumentTypePrimitive = true;
			pc.schema = new NumberSchema();
		} else if (Boolean.class.isAssignableFrom(argumentClass)) {
			pc.type = "boolean";
			pc.format = argumentClass.getSimpleName().toLowerCase();
			pc.isArgumentTypePrimitive = true;
			pc.schema = new BooleanSchema();
		} else if (MultipartFile.class.isAssignableFrom(argumentClass)) {
			pc.format = "File";
			pc.type = argumentClass.getSimpleName();
			pc.isArgumentTypePrimitive = true;
			pc.schema = new FileSchema();
		} else if (Date.class.isAssignableFrom(argumentClass)) {
			// pc.format = "File";
			pc.type = "date";
			pc.isArgumentTypePrimitive = true;
			DateSchema ds = new DateSchema();
			ds.setFormat(pc.format);
			pc.schema = ds;
		} else {
			pc.type = "object";
			pc.format = argumentClass.getSimpleName();
			pc.isArgumentTypePrimitive = false;
			pc.schema = new ObjectSchema();
			pc.ref = TypeNameUtils.getTypeName(parameterType);
			if (pc.ref.contains("<")) {
				pc.ref = pc.ref.replace("<", "").replace(">", "");
			}
			pc.schema.set$ref("#/components/schemas/" + pc.ref);
		}

	}

	static Set<Class<?>> resolvedClass = new HashSet<>();

	public static void resolveClassFields(Class<?> clazz, OpenAPI swagger) {
		if (resolvedClass.contains(clazz)) {
			return;
		}
		resolvedClass.add(clazz);
		if (!BeanUtils.isSimpleProperty(clazz)) {
			Field[] fields = clazz.getDeclaredFields();
			if (fields != null) {
				for (Field f : fields) {
					Type type = f.getGenericType();
					resolveType(type, swagger);
				}
			}
		}
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
		if (type instanceof Class<?>) {
			Class<?> clazz = (Class<?>) type;
			resolveClassFields(clazz, swagger);
		}

		String firstKey = null;
		for (String key : models.keySet()) {
			firstKey = key;
			Schema model = models.get(key);
			Map<String, Schema> properties = new HashMap<>();
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
				String propertyComment = getPropertyComment(type, name);
				if (propertyComment != null) {
					ss.setDescription(propertyComment);
				}
				ss.setName(name);
				properties.put(name, ss);
			});
			/**
			 * if(!CollectionUtils.isEmpty(model.getRequired())) {
			 * model.getRequired().clear(); }
			 */
			model.getProperties().clear();
			model.setProperties(properties);
			swagger.schema(key, model);
		}
		return firstKey;
	}

	private static String getPropertyComment(Type type, String propertyName) {

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
		FieldInfo fi = CommonClassCommentParser.fieldComments.get(fcKey);
		if (fi != null) {
			return fi.comment;
		} else {
			return null;
		}
	}
	
	
	public static boolean mayRequestBody(String httpMethod) {
		//CONNECT
		boolean mustNotHasBody=httpMethod.equalsIgnoreCase(RequestMethod.GET.name()) ||
				httpMethod.equalsIgnoreCase(RequestMethod.HEAD.name()) ||
				httpMethod.equalsIgnoreCase(RequestMethod.DELETE.name()) || 
				 false;
				//httpMethod.equalsIgnoreCase(RequestMethod.CONNECT.name()) ||
				//httpMethod.equalsIgnoreCase(RequestMethod.GET.name());
		return !mustNotHasBody;
	}

}
