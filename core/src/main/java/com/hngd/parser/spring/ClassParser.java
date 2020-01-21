package com.hngd.parser.spring;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.springframework.beans.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.hngd.constant.HttpParameterLocation;
import com.hngd.exception.ClassParseException;
import com.hngd.openapi.entity.HttpInterface;
import com.hngd.openapi.entity.HttpParameter;
import com.hngd.openapi.entity.ModuleInfo;
import com.hngd.parser.entity.ClassInfo;
import com.hngd.parser.entity.MethodInfo;
import com.hngd.parser.entity.ParameterInfo;
import com.hngd.parser.javadoc.extension.DescriptionBlock;
import com.hngd.parser.javadoc.extension.SummaryBlock;
import com.hngd.parser.javadoc.extension.TagsBlock;
import com.hngd.parser.source.ParserContext;
import com.hngd.parser.spring.parameter.CookieValueProcessor;
import com.hngd.parser.spring.parameter.HttpParameterProcessor;
import com.hngd.parser.spring.parameter.MatrixVariableProcessor;
import com.hngd.parser.spring.parameter.PathVariableProcessor;
import com.hngd.parser.spring.parameter.RequestBodyProcessor;
import com.hngd.parser.spring.parameter.RequestHeaderProcessor;
import com.hngd.parser.spring.parameter.RequestParamProcessor;
import com.hngd.parser.spring.parameter.RequestPartProcessor;
import com.hngd.utils.ClassUtils;
import com.hngd.utils.RestClassUtils;
import com.hngd.utils.TypeUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClassParser {

	ParserContext parserContext;
	List<HttpParameterProcessor> httpParameterProcessors=new ArrayList<>();
	public ClassParser(ParserContext parserContext) {
		this.parserContext = parserContext;
		httpParameterProcessors.add(new RequestParamProcessor());
		httpParameterProcessors.add(new PathVariableProcessor());
		httpParameterProcessors.add(new RequestBodyProcessor());
		httpParameterProcessors.add(new RequestHeaderProcessor());
		httpParameterProcessors.add(new RequestPartProcessor());
		httpParameterProcessors.add(new CookieValueProcessor());
		httpParameterProcessors.add(new MatrixVariableProcessor());
		

	}
 
	public Optional<ModuleInfo> parseModule(Class<?> cls) {
		ModuleInfo mi=null;
		try {
		    mi=doConvertClassToModule(cls);
		}catch(Exception e) {
			if(e instanceof ClassParseException) {
				throw e;
			}else {
				throw new ClassParseException(cls,e);
			}
		}
		return Optional.ofNullable(mi);
	}
	private ModuleInfo doConvertClassToModule(Class<?> cls) {
		log.info("start to process class:{}",cls.getName());
		if (!SpringAnnotationUtils.isControllerClass(cls)) {
			log.info("There is no annotation Controller or RestController for class:{}",cls.getName());
			return null;
		}
		ModuleInfo mi = new ModuleInfo();
		
		//parse module base info
		RequestMapping requestMapping = cls.getAnnotation(RequestMapping.class);
		mi.setUrl(SpringAnnotationUtils.extractUrl(requestMapping));
		mi.setName(cls.getSimpleName());
		mi.setSimpleClassName(cls.getSimpleName());
		mi.setCanonicalClassName(cls.getName());
		mi.setDeprecated(ClassUtils.isClassDeprecated(cls));
		ClassInfo classInfo=parserContext.getClassComment(cls);
		if(classInfo!=null) {
			mi.setComment(classInfo.getComment());
		}
		//parse module interfaces
		Method[] methods = cls.getDeclaredMethods();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			if(!RestClassUtils.isHttpInterface(method)) {
				log.debug("method:{}.{} has no http request annotation",
						cls.getName(),method.getName());
				continue;
			}
			Optional<HttpInterface> optionalInfo = parseMethod(method);
			optionalInfo.ifPresent(hi->{
				mi.getInterfaceInfos().add(hi);
				//attach class tags if exists
				if(classInfo!=null) {
				    Optional<TagsBlock> optionalTags=classInfo.findAnyExtension(TagsBlock.class);
				    	optionalTags.ifPresent(tagsBlock-> {
				    	tagsBlock.getPathItemTags().forEach(hi.getTags()::add);
				    });
				}
			});
		}
		return mi;
	}
	 
	private Optional<HttpInterface> parseMethod(Method method) {
		HttpInterface hi=null;
		try {
		    hi=doParseMethod(method);
		}catch(Exception e) {
			if(e instanceof ClassParseException) {
				throw e;
			}else {
				throw new ClassParseException(method.getDeclaringClass(),method,e);
			}
		}
		return Optional.ofNullable(hi);
	}
	private HttpInterface doParseMethod(Method method) {
		HttpInterface httpInterface = new HttpInterface();
		Optional<? extends Annotation> optionalAnnotation = RestClassUtils.getHttpRequestInfo(method);
		Annotation mappingAnnotation=optionalAnnotation.get();
		httpInterface.deprecated=isMethodDeprecated(method);
		//extract consumes
        List<String> consumes=RestClassUtils.extractCosumes(mappingAnnotation);
		if (consumes != null) {
			httpInterface.consumes = consumes;
		}
		//extract produces
		List<String> produces=RestClassUtils.extractProduces(mappingAnnotation);
		if (produces != null) {
			httpInterface.produces = produces;
		}
		//extract http url
        httpInterface.url =RestClassUtils.extractUrl(mappingAnnotation);
        //extract http method
		if(mappingAnnotation instanceof RequestMapping){
			RequestMethod[] methods=((RequestMapping)mappingAnnotation).method();
			if(methods==null || methods.length==0) {
				String methodKey=ClassUtils.getMethodIdentifier(method);
				log.error("The RequestMapping annotation for method:{} has a empty method value",methodKey);
			    throw new RuntimeException("方法"+methodKey+"的RequestMapping注解缺少method值");
			}else {
				//TODO one java method only has one http method?
				httpInterface.httpMethod = methods[0].name();
			}
		}else{
			httpInterface.httpMethod = mappingAnnotation.annotationType().getSimpleName().replace("Mapping", "");
		}
		httpInterface.javaReturnTypeName = method.getReturnType().getSimpleName();
		//httpInterface.javaRetureType = method.getReturnType();
		httpInterface.javaMethodName = method.getName();
		httpInterface.javaReturnType = method.getGenericReturnType();
		Optional<MethodInfo> optionalMethodInfo=parserContext.getMethodInfo(method);
		if(optionalMethodInfo.isPresent()) {
			MethodInfo mi=optionalMethodInfo.get();
			httpInterface.comment=mi.getComment();
			httpInterface.respComment=mi.getRetComment();
			//SummaryBlock 
			Optional<SummaryBlock> optionalSummary=mi.findAnyExtension(SummaryBlock.class);
			String summary=mi.getComment();
			if(optionalSummary.isPresent()) {
				summary=optionalSummary.get().getContent();
			}
			httpInterface.setSummary(summary);
			//DescriptionBlock 
			Optional<DescriptionBlock> optionalDdescription=mi.findAnyExtension(DescriptionBlock.class);
			String description=mi.getComment();
			if(optionalDdescription.isPresent()) {
				description=optionalDdescription.get().getContent();
			}
			httpInterface.setDescription(description);
	   
			Optional<TagsBlock> optionalTags=mi.findAnyExtension(TagsBlock.class);
			if(optionalTags.isPresent()) {
				List<String> tags=optionalTags.get().getPathItemTags();
				tags.forEach(httpInterface.getTags()::add);
			}
		}
		//extract http parameters
		doProcessParamaters(method,httpInterface);
		
		return httpInterface;
	}
	private void doProcessParamaters(Method method, HttpInterface httpInterface) {
		Parameter[] parameters = method.getParameters();
		for (int i = 0; i < parameters.length; i++) {
			Parameter parameter = parameters[i];
			List<HttpParameter> httpParams=processParameter(parameter);
			if(!CollectionUtils.isEmpty(httpParams)) {
				final int indexInJavaMethod=i;
				httpParams.stream()
				    .forEach(httpParam->httpParam.indexInJavaMethod=indexInJavaMethod);
				Optional<MethodInfo>  optionalMethodInfo=parserContext.getMethodInfo(method);
				if(optionalMethodInfo.isPresent()) {
					List<ParameterInfo> javaParameterInfos=optionalMethodInfo.get().getParameters();
					httpParams.stream()
					  .filter(hp->hp.getComment()==null)
					  .forEach(hp->{
						  String comment=javaParameterInfos.get(indexInJavaMethod).getComment();
						  hp.setComment(comment);
					  });
				}
				httpInterface.httpParameters.addAll(httpParams);
				//根据第一个参数判定的原因是,一个MultipartFile类型的java参数只能解析出一个http请求参数,
				//所以只要判定第一个参数就好了
				HttpParameter firstHttpParameter=httpParams.get(0);
				if (!httpInterface.isMultipart) {
					httpInterface.isMultipart = TypeUtils.isMultipartType(firstHttpParameter.getJavaType());
				}
				httpInterface.hasRequestBody=!firstHttpParameter.getLocation().isParameter();
			}else {
				String parameterKey=ClassUtils.getParameterIdentifier(parameter);
				log.warn("the http parameters extracted from {} is empty",parameterKey);
			}
			
		}
		
	}
	private static boolean isMethodDeprecated(Method method) {
		return method.getAnnotation(Deprecated.class)!=null;
	}

	/**
	 * @see <a href="https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/web.html#mvc-ann-arguments">Spring MVC Method Arguments</a> 
	 * @param parameter
	 * @param indexInJavaMethod 
	 * @return
	 */
	private List<HttpParameter> processParameter(Parameter parameter) {
        
		Optional<HttpParameterProcessor> optionalProcessor= httpParameterProcessors.stream()
            .filter(p->p.accept(parameter))
            .findFirst();
		if(optionalProcessor.isPresent()) {
			return optionalProcessor.get().process(parameter);
		}
        //如果是Spring自动注入的参数类型就直接返回
		Type paramType= parameter.getParameterizedType();
		if (MethodArgUtils.isSpringAutoInjectParameterType(paramType)) {
		    return null;
		}
		//For access to the model that is used in HTML controllers and exposed to templates as part of view rendering.
		if(MethodArgUtils.isModelParameterType(paramType)){
			return null;
		}
		if (parameter.getType() instanceof Class && BeanUtils.isSimpleProperty((Class<?>) parameter.getType())) {
			// requestparam
			HttpParameter httpParam = new HttpParameter();
			httpParam.name = parameter.getName();
			httpParam.location = HttpParameterLocation.query;
			httpParam.required = false;
			httpParam.javaType = parameter.getType();
			Annotation[] annotations=parameter.getAnnotations();
			Optional<String> dateFormat = MethodArgUtils.extractDateFormat(annotations);
			if (dateFormat.isPresent()) {
				httpParam.openapiFormat = dateFormat.get();
			}
			httpParam.isPrimitive = BeanUtils.isSimpleProperty(parameter.getType());
			return Arrays.asList(httpParam);
		}else{
			//model
			return extractParametersFromModel(parameter);
		}
		//ClassParseException.throwParameterParseException(parameter, "Not Supported Parameter Type",null); 
		 
		 
	}
	private Optional<String> extractPropertyDateFormat(Class<?> clazz,PropertyDescriptor pd) {
		String propertyName=pd.getName();
		Field field=ReflectionUtils.findField(clazz, propertyName);
		if(field==null) {
			log.error("field for property:{} is not found",propertyName);
			return Optional.empty();
		}
		DateTimeFormat a=field.getAnnotation(DateTimeFormat.class);
		if(a!=null) {
			return Optional.ofNullable(a.pattern());
		}
		return Optional.empty();
	}
	private boolean isPropertyRequired(Class<?> clazz,PropertyDescriptor pd) {
		Method readMethod=pd.getReadMethod();
		if(readMethod!=null) {
			NotNull notNullConstraint=readMethod.getAnnotation(NotNull.class);
			if(notNullConstraint!=null) {
				return true;
			}
		}
		String propertyName=pd.getName();
		Field field=ReflectionUtils.findField(clazz, propertyName);
		if(field!=null) {
			NotNull notNullConstraint=field.getAnnotation(NotNull.class);
			if(notNullConstraint!=null) {
				return true;
			}
		}
		return false;
	}
	public List<HttpParameter> extractParametersFromModel(Parameter parameter){
		Class<?> clazz=parameter.getType();
		PropertyDescriptor[] propertyDescriptors=BeanUtils.getPropertyDescriptors(clazz);
		List<HttpParameter> httpParams=new LinkedList<>();
		for(PropertyDescriptor property:propertyDescriptors) {
			HttpParameter httpParam = new HttpParameter();
			httpParam.name = property.getName();
			Field field=ReflectionUtils.findField(clazz,httpParam.name);
			if(field==null) {
				log.warn("the property {} is read only",httpParam.name);
				continue;
			}
			//TODO need to analysis method mapping url
			httpParam.location = HttpParameterLocation.query;
			httpParam.required = isPropertyRequired(clazz,property);
			httpParam.javaType=property.getPropertyType();
			httpParam.comment=parserContext.getFieldComment(field);
			Optional<String> dateFormat=extractPropertyDateFormat(clazz,property);
			if(dateFormat.isPresent()) {
				httpParam.openapiFormat=dateFormat.get();
			}
			httpParam.isPrimitive=BeanUtils.isSimpleProperty(parameter.getType());
			httpParams.add(httpParam);
		}
		return  httpParams;
	}

}
