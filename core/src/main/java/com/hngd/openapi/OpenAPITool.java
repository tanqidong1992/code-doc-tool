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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import com.hngd.constant.HttpParameterLocation;
import com.hngd.openapi.entity.HttpInterface;
import com.hngd.openapi.entity.HttpParameter;
import com.hngd.openapi.entity.ModuleInfo;
import com.hngd.constant.Comments;
import com.hngd.constant.Constants;
import com.hngd.parser.source.CommentStore;
import com.hngd.parser.spring.ClassParser;
import com.hngd.utils.ClassUtils;
import com.hngd.utils.ReflectionExtUtils;
import com.hngd.utils.TypeNameUtils;
import com.hngd.utils.TypeUtils;

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
	private ClassParser classParser;
    private TypeResolver typeResolver;
	public OpenAPITool(OpenAPI openAPI,CommentStore commentStore) {
		this.openAPI = openAPI;
		typeResolver=new TypeResolver(commentStore);
		if(CollectionUtils.isEmpty(openAPI.getPaths())) {
			Paths paths = new Paths();
			openAPI.setPaths(paths);
		}
		if(CollectionUtils.isEmpty(openAPI.getTags())) {
			List<Tag> tags = new ArrayList<Tag>();
			openAPI.setTags(tags);
		}
		this.classParser=new ClassParser(commentStore);
	}
    /**
     * 解析指定包名(包含子包)下的所有类
     * @param basePackageName 包名
     */
	public void parse(String basePackageName) {
		List<Class<?>> clses = ClassUtils.getClassBelowPacakge(basePackageName);
		parse(clses);
	}

	public void parse(List<Class<?>> clses) {

		clses.stream()
		    .map(classParser::parseModule)
		    .filter(Optional::isPresent)
		    .map(Optional::get)
			.forEach(this::buildPaths);
	}
	public void parse(Class<?>... clazz) {
		for(Class<?> c : clazz) {
			Optional<ModuleInfo> optionalModule=classParser.parseModule(c);
			optionalModule.ifPresent(this::buildPaths);	
		}
	}
	private void buildPaths(ModuleInfo moduleInfo) {
		String classComment = moduleInfo.getComment();
		if (StringUtils.isBlank(classComment)) {
			logger.warn("the comment for class:{} is empty", moduleInfo.getCanonicalClassName());
		}
		String moduleTagName = StringUtils.isNotBlank(classComment) ? 
				classComment : moduleInfo.getSimpleClassName();
		Tag moduleTag = new Tag().name(moduleTagName);
		openAPI.getTags().add(moduleTag);
		for (HttpInterface interfaceInfo : moduleInfo.getInterfaceInfos()) {
			PathItem pathItem = buildPathItem(moduleInfo, interfaceInfo, moduleTag);
			if (pathItem == null) {
				continue;
			}
			String pathKey = moduleInfo.getUrl() + interfaceInfo.getUrl();//+interfaceInfo.httpMethod;
			addPathItemToPaths(pathKey,pathItem);
		}
	}
    private void addPathItemToPaths(String pathKey,PathItem pathItem) {
    	Paths paths=openAPI.getPaths();
    	if(paths.containsKey(pathKey)) {
			PathItem oldPath=paths.get(pathKey);
			Map<HttpMethod, Operation> operations=pathItem.readOperationsMap();
			operations.forEach((httpMethod,operation)->oldPath.operation(httpMethod, operation));
		}else {
			paths.put(pathKey, pathItem);
		}
    }
    
	public static Parameter createParameter(HttpParameter parameter) {
		if (parameter.getLocation().isParameter()) {
			Parameter param =(Parameter) ReflectionExtUtils
					.newInstance(parameter.location.getParamClass());
			param.setName(parameter.name);
			param.setRequired(parameter.required);
			if(parameter.isPrimitive()) {
				param.setSchema(parameter.schema);
			}else {
				Content content=new Content();
				MediaType value=new MediaType();
				value.setSchema(parameter.schema);
				content.put(Constants.DEFAULT_CONSUME_TYPE, value);
				param.setContent(content);
			}
			param.setDescription(parameter.comment);
			return param;
		} else {
			return null;
		}
	}

	private PathItem buildPathItem(ModuleInfo moduleInfo, HttpInterface httpInterface, Tag moduleTag) {
 
		PathItem path = new PathItem();
		Operation op = new Operation();
		List<String> operationTags = new ArrayList<>();
		//如果接口本身就含有tag信息,则不使用模块的tag
        if(!CollectionUtils.isEmpty(httpInterface.getTags())) {
        	httpInterface.getTags().forEach(operationTags::add);
        }else {
		    operationTags.add(moduleTag.getName());
        }
		op.setDeprecated(moduleInfo.getDeprecated() || httpInterface.deprecated);
		op.setTags(operationTags);
		String operationId=buildOperationId(moduleInfo,httpInterface);
		op.setOperationId(operationId);
		op.setDescription(httpInterface.getDescription());
        op.setSummary(httpInterface.getSummary());
		List<Parameter> parameters = new ArrayList<>();
 
		if (!hasRequestBody(httpInterface.httpMethod)) {
			//对于不在requestbody中的参数
			parameters=processHttpParameter(httpInterface.httpParameters);
		} else{
			MediaType mediaTypeContent = new MediaType();
			Content content = new Content();
			//requestbody只有一部分的情况
			if (httpInterface.hasRequestBody && !httpInterface.isMultipart()) {
				
				List<String> consumes=httpInterface.getConsumes();
				if(CollectionUtils.isEmpty(consumes)) {
					content.addMediaType(Constants.DEFAULT_CONSUME_TYPE, mediaTypeContent);
				}else {
					for(String consume:consumes) {
						content.addMediaType(consume, mediaTypeContent);
					}
				}
				//RequestBody只有一部分,所以被RequestBody注解修饰的方法参数只有一个
				List<HttpParameter>  httpParameters=httpInterface.getHttpParameters();
				Optional<HttpParameter> optionalParameterInBody=httpParameters.stream()
				  .filter(hp->!hp.getLocation().isParameter())
				  .findFirst();
				
				if(optionalParameterInBody.isPresent()) {
					HttpParameter pc = optionalParameterInBody.get();
					Type parameterType = pc.getJavaType();
					resolveParameterInfo(pc, parameterType);
					String key = typeResolver.resolveAsSchema(parameterType, openAPI);
					//如果是简单类型就会返回null
					if(!StringUtils.isEmpty(key)) {
						Schema<?> schema = openAPI.getComponents().getSchemas().get(key);
						mediaTypeContent.setSchema(schema);
					}else {
						Schema<?> paramSchema=pc.schema;
						mediaTypeContent.setSchema(paramSchema);
						if(paramSchema!=null && StringUtils.isEmpty(paramSchema.getDescription())) {
						    pc.schema.setDescription(pc.comment);
						}
					}
				}
				List<HttpParameter> filterHttpParameters=httpParameters.stream()
				.filter(hp->hp.getLocation().isParameter())
				.collect(Collectors.toList());
				if(!CollectionUtils.isEmpty(filterHttpParameters)) {
					parameters=processHttpParameter(filterHttpParameters);
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
					Type parameterType = pc.getJavaType();
					resolveParameterInfo(pc, parameterType);
					if (!pc.isPrimitive) {
						typeResolver.resolveAsSchema(parameterType, openAPI);
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
					if (pc.location.equals(HttpParameterLocation.query)) {
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
						
					} else if (pc.location.equals(HttpParameterLocation.path)) {
						Parameter pathParameter = createParameter(pc);
						parameters.add(pathParameter);
					}else if (pc.location.equals(HttpParameterLocation.body)) {
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
		resolveResponse(op, httpInterface);
		String httpMethod=httpInterface.httpMethod;
		addOpToPath(op,path,httpMethod);
		return path;

	}

	private List<Parameter> processHttpParameter(List<HttpParameter> httpParameters) {
		List<Parameter> parameters=new ArrayList<>();
		for (int i = 0; i < httpParameters.size(); i++) {
			HttpParameter pc = httpParameters.get(i);
			Type parameterType = pc.getJavaType();
			resolveParameterInfo(pc, parameterType);
			if (!pc.isPrimitive) {
				typeResolver.resolveAsSchema(parameterType, openAPI);
			}
			Parameter param = createParameter(pc);
			parameters.add(param);
		}
		return parameters;
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

	private void resolveResponse(Operation op, HttpInterface interfaceInfo) {
		ApiResponses responses = new ApiResponses();
		ApiResponse resp = new ApiResponse();
		MediaType mt = new MediaType();
		if(!Void.class.equals(interfaceInfo.javaReturnType)) {
			String firstKey = typeResolver.resolveAsSchema(interfaceInfo.javaReturnType, openAPI);
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
		String respComment=interfaceInfo.respComment;
		if(StringUtils.isEmpty(respComment)) {
			respComment=Comments.DEFAULT_RESPONSE_DESCRIPTION;
		}
		resp.setDescription(respComment);
		responses.put(HttpStatus.OK.value()+"", resp);
		op.setResponses(responses);
	}

	private static void resolveParameterInfo(HttpParameter pc, Type parameterType) {
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
		} else if (Date.class.isAssignableFrom(argumentClass) 
				|| LocalDate.class.isAssignableFrom(argumentClass)
				|| LocalDateTime.class.isAssignableFrom(argumentClass)) {
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
