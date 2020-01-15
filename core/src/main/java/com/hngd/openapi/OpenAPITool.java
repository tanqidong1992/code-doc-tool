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

package com.hngd.openapi;

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
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import com.hngd.constant.HttpParameterIn;
import com.hngd.openapi.entity.HttpInterface;
import com.hngd.openapi.entity.HttpParameter;
import com.hngd.constant.Comments;
import com.hngd.constant.Constants;
import com.hngd.parser.clazz.ClassParser;
import com.hngd.parser.entity.FieldInfo;
import com.hngd.parser.entity.MethodInfo;
import com.hngd.parser.entity.ModuleInfo;
import com.hngd.parser.entity.ParameterInfo;
import com.hngd.parser.source.CommonClassCommentParser;
import com.hngd.utils.ClassUtils;
import com.hngd.utils.TypeNameUtils;
import com.hngd.utils.TypeUtils;

import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.PathItem.HttpMethod;
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
		clses.stream()
		    .map(clazz -> ClassParser.parseModule(clazz))
		    .filter(Optional::isPresent)
		    .map(Optional::get)
			.forEach(mi -> buildPaths(mi, paths, tags));
	}

	private void buildPaths(ModuleInfo moduleInfo, Paths paths, List<Tag> tags) {
		String classComment = CommonClassCommentParser.classComments.get(moduleInfo.getSimpleClassName());
		if (StringUtils.isBlank(classComment)) {
			logger.warn("the comment for class:{} is empty", moduleInfo.getCanonicalClassName());
		}
		String moduleTagName = StringUtils.isNotBlank(classComment) ? 
				classComment : moduleInfo.getSimpleClassName();
		Tag moduleTag = new Tag().name(moduleTagName);
		tags.add(moduleTag);
		for (HttpInterface interfaceInfo : moduleInfo.getInterfaceInfos()) {
			PathItem pathItem = buildPathItem(moduleInfo, interfaceInfo, moduleTag);
			if (pathItem == null) {
				continue;
			}
			String pathKey = moduleInfo.getUrl() + interfaceInfo.url;//+interfaceInfo.httpMethod;
			addPathItemToPaths(paths,pathKey,pathItem);
		}
	}
    public static void addPathItemToPaths(Paths paths,String pathKey,PathItem pathItem) {
    	if(paths.containsKey(pathKey)) {
			PathItem oldPath=paths.get(pathKey);
			Map<HttpMethod, Operation> operations=pathItem.readOperationsMap();
			operations.forEach((httpMethod,operation)->oldPath.operation(httpMethod, operation));
		}else {
			paths.put(pathKey, pathItem);
		}
    }
	public static Parameter createParameter(HttpParameter pc) {
		if (pc.httpParamIn.isParameter()) {
			Parameter param = null;
			try {
				param = (Parameter) pc.httpParamIn.getParamClass().newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				logger.error("", e);
			}
			param.setName(pc.name);
			param.setRequired(pc.required);
			
			if(pc.isPrimitive()) {
				param.setSchema(pc.schema);
			}else {
				Content content=new Content();
				MediaType value=new MediaType();
				value.setSchema(pc.schema);
				content.put(Constants.DEFAULT_CONSUME_TYPE, value);
				param.setContent(content);
			}
			 
			param.setDescription(pc.comment);
			return param;
		} else {
			return null;
		}
	}

	private PathItem buildPathItem(ModuleInfo moduleInfo, HttpInterface httpInterface, Tag tag) {

		String methodKey = moduleInfo.getSimpleClassName() + "#" + httpInterface.getJavaMethodName();
		MethodInfo methodComment = CommonClassCommentParser.methodComments.get(methodKey);
		if (methodComment == null || methodComment.getParameters() == null) {
			logger.warn("the method comment for method:{} is empty", methodKey);
			return null;
		}
		httpInterface.comment=methodComment.getComment();
		PathItem path = new PathItem();
		Operation op = new Operation();
		List<String> operationTags = new ArrayList<>();
        
		operationTags.add(tag.getName());
		op.setDeprecated(moduleInfo.getDeprecated() || httpInterface.deprecated);
		op.setTags(operationTags);
		String operationId=buildOperationId(moduleInfo,httpInterface);
		op.setOperationId(operationId);
		op.setDescription(httpInterface.comment);
        op.setSummary(httpInterface.comment);
		List<Parameter> parameters = new ArrayList<>();

		if (!hasRequestBody(httpInterface.httpMethod)) {
			for (int i = 0; i < httpInterface.httpParameters.size(); i++) {
				HttpParameter pc = httpInterface.httpParameters.get(i);
				String parameterComment = null;
				if (pc.indexInJavaMethod < methodComment.getParameters().size()) {
					ParameterInfo pi = methodComment.getParameters().get(pc.indexInJavaMethod);
					parameterComment = pi.getComment();
				}
				if(StringUtils.isNotEmpty(parameterComment) && StringUtils.isEmpty(pc.comment)) {
				    pc.comment =parameterComment;
				}
				Type parameterType = pc.getJavaType();
				resolveParameterInfo(pc, parameterType);
				if (!pc.isPrimitive) {
					resolveType(parameterType, openAPI);
				}
				Parameter param = createParameter(pc);
				parameters.add(param);
			}

		} else{
			MediaType mediaTypeContent = new MediaType();
			Content content = new Content();
			if (httpInterface.hasRequestBody && !httpInterface.isMultipart()) {
				
				List<String> consumes=httpInterface.getConsumes();
				if(CollectionUtils.isEmpty(consumes)) {
					content.addMediaType(Constants.DEFAULT_CONSUME_TYPE, mediaTypeContent);
				}else {
					for(String consume:consumes) {
						content.addMediaType(consume, mediaTypeContent);
					}
				}
				
				HttpParameter pc = httpInterface.getHttpParameters().get(0);
				Type parameterType = pc.getJavaType();
				resolveParameterInfo(pc, parameterType);
				String key = resolveType(parameterType, openAPI);
				//如果是简单类型就会返回null
				if(!StringUtils.isEmpty(key)) {
					Schema<?> schema = openAPI.getComponents().getSchemas().get(key);
					mediaTypeContent.setSchema(schema);
				}else {
					mediaTypeContent.setSchema(pc.schema);
				}
				
			} else {
				if (httpInterface.isMultipart()) {
					content.addMediaType(Constants.MULTIPART_FORM_DATA, mediaTypeContent);
				} else {
					content.addMediaType(Constants.APPLICATION_FORM_URLENCODED_VALUE, mediaTypeContent);
				}
				ObjectSchema contentSchema = new ObjectSchema();
				mediaTypeContent.setSchema(contentSchema);
				Map<String, Encoding> contentEncodings = new HashMap<>();
				mediaTypeContent.setEncoding(contentEncodings);
				for (int i = 0; i < httpInterface.httpParameters.size(); i++) {
					HttpParameter pc = httpInterface.httpParameters.get(i);
					if (pc.indexInJavaMethod < methodComment.getParameters().size()) {
						ParameterInfo pi = methodComment.getParameters().get(pc.indexInJavaMethod);
						if(StringUtils.isNotEmpty(pi.getComment()) && StringUtils.isEmpty( pc.comment)) {
						    pc.comment = pi.getComment();
						}
						
					}
					Type parameterType = pc.getJavaType();
					resolveParameterInfo(pc, parameterType);
					if (!pc.isPrimitive) {
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
								encoding.setContentType(Constants.MULTIPART_FILE_TYPE);
							} else {
								encoding.setContentType(Constants.DEFAULT_CONSUME_TYPE);
							}
						}
					} else {
						if (TypeUtils.isMultipartType(parameterType)) {
							encoding.setContentType(Constants.MULTIPART_FILE_TYPE);
						} else {
							encoding.setContentType(Constants.DEFAULT_CONSUME_TYPE);
						}
					}
					contentEncodings.put(pc.name, encoding);
					
					if (pc.httpParamIn.equals(HttpParameterIn.query)) {
						// item.setSchema(pc.schema);
			 
						if (pc.isRequired()) {
							contentSchema.addRequiredItem(pc.name);
						}
						if (TypeUtils.isMultipartType(parameterType)) {
							Schema<?> propertiesItem = new Schema<>();
							 propertiesItem.format("binary");
							 propertiesItem.setType("string");
							 
							 Class<?> type=(Class<?>) parameterType;
							 if(type.isArray()) {
								 ArraySchema as=new ArraySchema();
								 //as.setDescription(pc.comment);
								 as.setType("array");
								 Schema<?> items=new Schema<>();
								 items.setType("string");
								 items.setFormat("binary");
								 as.setItems(items);
								 propertiesItem=as;
							 }
							 propertiesItem.setDescription(pc.comment);
							 contentSchema.addProperties(pc.name, propertiesItem);
						} else {
							pc.schema.setDescription(pc.comment);
							contentSchema.addProperties(pc.name, pc.schema);
						}
						
					} else if (pc.httpParamIn.equals(HttpParameterIn.path)) {
						Parameter pathParameter = createParameter(pc);
						parameters.add(pathParameter);
					}else if (pc.httpParamIn.equals(HttpParameterIn.body)) {
						Schema<?> propertiesItem = new Schema<>();
						propertiesItem.setDescription(pc.comment);
						propertiesItem.setType(pc.openapiType);
						propertiesItem.set$ref(pc.ref);
						if (pc.ref != null && pc.schema instanceof ObjectSchema) {
							@SuppressWarnings("rawtypes")
							Map<String, Schema> properties = new HashMap<>();
							properties.put(pc.ref, pc.schema);
							propertiesItem.setProperties(properties);
						}
						if (pc.isRequired()) {
							contentSchema.addRequiredItem(pc.name);
						}
						if (TypeUtils.isMultipartType(parameterType)) {
							propertiesItem.format("binary");
							propertiesItem.setType("string");
							 Class<?> type=(Class<?>) parameterType;
							 if(type.isArray()) {
								 ArraySchema as=new ArraySchema();
								 as.setDescription(pc.comment);
								 as.setType("array");
								 Schema<?> items=new Schema<>();
								 items.setType("string");
								 items.setFormat("binary");
								 as.setItems(items);
								 propertiesItem=as;
							 }
						} else {
							propertiesItem.format(pc.openapiFormat);
						}
						contentSchema.addProperties(pc.name, propertiesItem);
					}
				}
			}
			RequestBody requestBody = new RequestBody();
			requestBody.setContent(content);
			op.setRequestBody(requestBody);
		}
		op.setParameters(parameters);
		resolveResponse(op, httpInterface, methodComment != null ? methodComment.getRetComment() : null);
		String httpMethod=httpInterface.httpMethod;
		addOpToPath(op,path,httpMethod);
		return path;

	}

	private String buildOperationId(ModuleInfo moduleInfo, HttpInterface interfaceInfo) {
		String s=moduleInfo.getCanonicalClassName()+"#"+interfaceInfo.getJavaMethodName();
		return s.replaceAll("\\.", "#");
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

	private void resolveResponse(Operation op, HttpInterface interfaceInfo, String respComment) {
		ApiResponses responses = new ApiResponses();
		ApiResponse resp = new ApiResponse();
		MediaType mt = new MediaType();
		if(!Void.class.equals(interfaceInfo.javaReturnType)) {
			String firstKey = resolveType(interfaceInfo.javaReturnType, openAPI);
			if(firstKey!=null) {
				Schema<?> schema = new ObjectSchema();
				schema.set$ref("#/components/schemas/" + firstKey);
				mt.setSchema(schema);
			}
			
		}
		Content respContent = new Content();
		List<String> ps=interfaceInfo.produces;
		if(!CollectionUtils.isEmpty(ps)) {
			for(String p:ps) {
				respContent.put(p, mt);
			}
		}else {
			respContent.put(Constants.DEFAULT_CONSUME_TYPE, mt);
		}
 
		resp.setContent(respContent);
		
		if(StringUtils.isEmpty(respComment)) {
			respComment=Comments.DEFAULT_RESPONSE_DESCRIPTION;
		}
		resp.setDescription(respComment);
		responses.put(HttpStatus.OK.value()+"", resp);
		op.setResponses(responses);
	}

	private void resolveParameterInfo(HttpParameter pc, Type parameterType) {
		Class<?> argumentClass = null;
		if (parameterType instanceof ParameterizedType) {
			ParameterizedType ppt = (ParameterizedType) parameterType;
			Type rawType = ppt.getRawType();
			Class<?> argumentType = (Class<?>) ppt.getActualTypeArguments()[0];
			if (rawType instanceof Class<?>) {
				Class<?> rawClass = (Class<?>) rawType;
				if (Collection.class.isAssignableFrom(rawClass)) {
					pc.isCollection = true;
					pc.javaParameterizedType=argumentType;
					if(!BeanUtils.isSimpleProperty(argumentType)) {
					    pc.ref = TypeNameUtils.getTypeName(argumentType);
					} 
				}else if(Map.class.isAssignableFrom(rawClass)) {
					//TODO parse map 
				}
			}
			argumentClass=(Class<?>) rawType;
			
		} else {
			argumentClass = (Class<?>) parameterType;
		}
		if(argumentClass==null) {
			logger.debug("");
		}
		if(pc.isCollection){
			pc.openapiType="array";
			ArraySchema as=new ArraySchema();
			pc.schema=as;
			ObjectSchema items=new ObjectSchema();
			as.setItems(items);
			
			if(pc.ref!=null) {
				if (pc.ref.contains("<")) {
					pc.ref = pc.ref.replace("<", "").replace(">", "").replace(",", "");
				}
			    items.set$ref("#/components/schemas/" + pc.ref);
			}else {
				SimpleTypeFormat tf=SimpleTypeFormat.convert(pc.javaParameterizedType);
				items.setType(tf.getType());
				items.setFormat(tf.getFormat());
			}
			
		}else if (String.class.isAssignableFrom(argumentClass)) {
			pc.openapiFormat = "string";
			pc.openapiType = "string";
			pc.isPrimitive = true;
			pc.schema = new StringSchema();
		} else if (Number.class.isAssignableFrom(argumentClass)) {
			pc.openapiType = "number";
			pc.openapiFormat = argumentClass.getSimpleName().toLowerCase();
			pc.isPrimitive = true;
			pc.schema = new NumberSchema();
		} else if (Boolean.class.isAssignableFrom(argumentClass)) {
			pc.openapiType = "boolean";
			pc.openapiFormat = argumentClass.getSimpleName().toLowerCase();
			pc.isPrimitive = true;
			pc.schema = new BooleanSchema();
		} else if (MultipartFile.class.isAssignableFrom(argumentClass)) {
			pc.openapiFormat = "File";
			pc.openapiType = argumentClass.getSimpleName();
			pc.isPrimitive = true;
			pc.schema = new FileSchema();
		} else if (Date.class.isAssignableFrom(argumentClass)) {
			// pc.format = "File";
			pc.openapiType = "date";
			pc.isPrimitive = true;
			DateSchema ds = new DateSchema();
			ds.setFormat(pc.openapiFormat);
			pc.schema = ds;
		}else if(Map.class.isAssignableFrom(argumentClass) || MultiValueMap.class.isAssignableFrom(argumentClass)) {
			pc.openapiType = "object";
			pc.openapiFormat = argumentClass.getSimpleName();
			pc.isPrimitive = false;
			pc.schema = new ObjectSchema();
		}else{
			pc.openapiType = "object";
			pc.openapiFormat = argumentClass.getSimpleName();
			pc.isPrimitive = false;
			pc.schema = new ObjectSchema();
			pc.ref = TypeNameUtils.getTypeName(parameterType);
			if (pc.ref.contains("<")) {
				pc.ref = pc.ref.replace("<", "").replace(">", "").replace(",", "");;
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
					if(type instanceof Class && BeanUtils.isSimpleProperty((Class)type)) {
						continue;
					}
					resolveType(type, swagger);
				}
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String resolveType(Type type, OpenAPI swagger) {
		Map<String, Schema> schemas = ModelConverters.getInstance().read(type);
		if (type instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) type;
			Type[] subTypes = pt.getActualTypeArguments();
			for (Type subType : subTypes) {
				String tempKey=resolveType(subType, swagger);
			}
			Type rawType=pt.getRawType();
			if(rawType instanceof Class<?>) {
				Class<?> rawClass=(Class<?>) rawType;
				if(rawClass.isArray() || Collection.class.isAssignableFrom(rawClass)) {
					
				}
			}
			 
			
		}
		if (type instanceof Class<?>) {
			Class<?> clazz = (Class<?>) type;
			if(!BeanUtils.isSimpleProperty(clazz)) {
				resolveClassFields(clazz, swagger);
			}
			
		}

		String firstKey = null;
		for (String key : schemas.keySet()) {
			firstKey = key;
			Schema model = schemas.get(key);
			swagger.schema(key, model);
			Map<String, Schema> properties = new HashMap<>();
			Map<String, Schema> cps = model.getProperties();
			if (cps == null) {
				continue;
			}
			cps.values().forEach(property -> {
				Schema schema =   property;
				String name = schema.getName();
				if (name.startsWith("get")) {
					name = name.replace("get", "");
				}
				String propertyType=schema.getType();
				String propertyComment = getPropertyComment(type, name);
				if (propertyComment != null) {
					schema.setDescription(propertyComment);
				}
				schema.setName(name);
				properties.put(name, schema);
			});
			model.getProperties().clear();
			model.setProperties(properties);
			
		}
		
		return firstKey;
	}

	private static String getPropertyComment(Type type, String propertyName) {

		Field field = null;
		if (type instanceof Class<?>) {
			field=ReflectionUtils.findField((Class<?>) type,propertyName);
			//String typeName = ((Class<?>) type).getSimpleName();
			//fcKey = typeName + "#" + propertyName;
			//CommonClassCommentParser.getFieldComment(field);
		} else if (type instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) type;
			// fcKey=name;
			//String typeName = ((Class<?>) pt.getRawType()).getSimpleName();
			//fcKey = typeName + "#" + propertyName;
			field=ReflectionUtils.findField((Class<?>) pt.getRawType(),propertyName);
		}
		if(field==null) {
			logger.error("Counld not found property:{} At Class:{}",propertyName,type.getTypeName());
			return null;
		}
		String comment = CommonClassCommentParser.getFieldComment(field);
		if (comment != null) {
			return comment;
		} else {
			return null;
		}
	}
	
	
	public static boolean hasRequestBody(String httpMethod) {
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
