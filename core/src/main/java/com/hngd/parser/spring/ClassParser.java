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

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hngd.constant.HttpParameterLocation;
import com.hngd.exception.ClassParseException;
import com.hngd.openapi.entity.HttpInterface;
import com.hngd.openapi.entity.HttpParameter;
import com.hngd.openapi.entity.ModuleInfo;
import com.hngd.parser.entity.ClassInfo;
import com.hngd.parser.entity.MethodInfo;
import com.hngd.parser.entity.ParameterInfo;
import com.hngd.parser.javadoc.extension.DescriptionBlockTag;
import com.hngd.parser.javadoc.extension.SummaryBlockTag;
import com.hngd.parser.javadoc.extension.TagsBlockTag;
import com.hngd.parser.source.CommentStore;
import com.hngd.parser.spring.parameter.CookieValueParameterExtractor;
import com.hngd.parser.spring.parameter.HttpParameterExtractor;
import com.hngd.parser.spring.parameter.MatrixVariableParameterExtractor;
import com.hngd.parser.spring.parameter.PathVariableParameterExtractor;
import com.hngd.parser.spring.parameter.RequestBodyParameterExtractor;
import com.hngd.parser.spring.parameter.RequestHeaderParameterExtractor;
import com.hngd.parser.spring.parameter.RequestParamParameterExtractor;
import com.hngd.parser.spring.parameter.RequestPartParameterExtractor;
import com.hngd.utils.ClassUtils;
import com.hngd.utils.RestClassUtils;
import com.hngd.utils.TypeUtils;

import lombok.extern.slf4j.Slf4j;
/**
 * Spring MVC接口类(Controller或者RestController注解修饰类)解析器
 * @author tqd
 *
 */
@Slf4j
public class ClassParser {

    private CommentStore commentStore;
    List<HttpParameterExtractor<?>> httpParameterProcessors=new ArrayList<>();
    public ClassParser(CommentStore commentStore) {
        this.commentStore = commentStore;
        httpParameterProcessors.add(new RequestParamParameterExtractor());
        httpParameterProcessors.add(new PathVariableParameterExtractor());
        httpParameterProcessors.add(new RequestBodyParameterExtractor());
        httpParameterProcessors.add(new RequestHeaderParameterExtractor());
        httpParameterProcessors.add(new RequestPartParameterExtractor());
        httpParameterProcessors.add(new CookieValueParameterExtractor());
        httpParameterProcessors.add(new MatrixVariableParameterExtractor());
    }
 
    public Optional<ModuleInfo> parseModule(Class<?> cls) {
        try {
            return doConvertClassToModule(cls);
        }catch(Exception e) {
            if(e instanceof ClassParseException) {
                throw e;
            }else {
                throw new ClassParseException(cls,e);
            }
        }
    }
    private Optional<ModuleInfo> doConvertClassToModule(Class<?> cls) {
        log.info("Start to process class:{}",cls.getName());
        if (!SpringAnnotationUtils.isControllerClass(cls)) {
            log.info("There Controller or RestController annotation for class:{} is not Found!",cls.getName());
            return Optional.empty();
        }
        ModuleInfo moduleInfo = new ModuleInfo();
        //parse module base info
        RequestMapping requestMapping = cls.getAnnotation(RequestMapping.class);
        moduleInfo.setUrl(SpringAnnotationUtils.extractUrl(requestMapping));
        moduleInfo.setName(cls.getSimpleName());
        moduleInfo.setSimpleClassName(cls.getSimpleName());
        moduleInfo.setCanonicalClassName(cls.getName());
        moduleInfo.setDeprecated(ClassUtils.isClassDeprecated(cls));
        ClassInfo classInfo=commentStore.getClassComment(cls);
        if(classInfo!=null) {
            moduleInfo.setComment(classInfo.getComment());
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
                moduleInfo.getInterfaceInfos().add(hi);
                //attach class tags to pathItem if exists
                if(classInfo!=null) {
                    Optional<TagsBlockTag> optionalTags=classInfo.findAnyExtension(TagsBlockTag.class);
                    optionalTags.ifPresent(tagsBlock-> {
                        tagsBlock.getPathItemTags().forEach(hi.getTags()::add);
                    });
                }
            });
        }
        return Optional.of(moduleInfo);
    }
     
    private Optional<HttpInterface> parseMethod(Method method) {
        try {
            return doParseMethod(method);
        }catch(Exception e) {
            if(e instanceof ClassParseException) {
                throw e;
            }else {
                throw new ClassParseException(method.getDeclaringClass(),method,e);
            }
        }
    }
    private Optional<HttpInterface> doParseMethod(Method method) {
        HttpInterface httpInterface = new HttpInterface();
        Optional<? extends Annotation> optionalAnnotation = RestClassUtils.getHttpRequestInfo(method);
        Annotation mappingAnnotation=optionalAnnotation.get();
        httpInterface.deprecated=isMethodDeprecated(method);
        //extract consumes
        httpInterface.consumes=RestClassUtils.extractCosumes(mappingAnnotation);
        //extract produces
        httpInterface.produces=RestClassUtils.extractProduces(mappingAnnotation);
        //extract http url
        httpInterface.url =RestClassUtils.extractUrl(mappingAnnotation);
        //extract http method
        Optional<String> httpMethod=RestClassUtils.extractHttpMethod(mappingAnnotation);
        if(httpMethod.isPresent()) {
            httpInterface.httpMethod = httpMethod.get();
        }else {
            String methodKey=ClassUtils.getMethodIdentifier(method);
            log.error("The RequestMapping annotation for method:{} has a empty method value",methodKey);
            throw new RuntimeException("The method "+methodKey+"的RequestMapping注解缺少method值");
        }
        httpInterface.javaReturnTypeName = method.getReturnType().getSimpleName();
        httpInterface.javaMethodName = method.getName();
        httpInterface.javaReturnType = method.getGenericReturnType();
        //attach comment to http interface
        Optional<MethodInfo> optionalMethodInfo=commentStore.getMethodInfo(method);
        optionalMethodInfo.ifPresent(mi->attachComment(httpInterface,mi));
        //extract http parameters
        doProcessParamaters(method,httpInterface);
        return Optional.of(httpInterface);
    }
    private void attachComment(HttpInterface httpInterface, MethodInfo mi) {
        httpInterface.comment=mi.getComment();
        httpInterface.respComment=mi.getRetComment();
        //SummaryBlock 
        Optional<SummaryBlockTag> optionalSummary=mi.findAnyExtension(SummaryBlockTag.class);
        String summary=mi.getComment();
        if(optionalSummary.isPresent()) {
            summary=optionalSummary.get().getContent();
        }
        httpInterface.setSummary(summary);
        //DescriptionBlock 
        Optional<DescriptionBlockTag> optionalDdescription=mi.findAnyExtension(DescriptionBlockTag.class);
        String description=mi.getComment();
        if(optionalDdescription.isPresent()) {
            description=optionalDdescription.get().getContent();
        }
        httpInterface.setDescription(description);
   
        Optional<TagsBlockTag> optionalTags=mi.findAnyExtension(TagsBlockTag.class);
        if(optionalTags.isPresent()) {
            List<String> tags=optionalTags.get().getPathItemTags();
            tags.forEach(httpInterface.getTags()::add);
        }
        
    }

    private void doProcessParamaters(Method method, HttpInterface httpInterface) {
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            //对于简单类型,将会返回一个参数,如果是复杂类型,可能会返回多个参数
            List<HttpParameter> httpParams=extractHttpParameter(parameter);
            if(!CollectionUtils.isEmpty(httpParams)) {
                final int indexInJavaMethod=i;
                httpParams.forEach(httpParam->httpParam.indexInJavaMethod=indexInJavaMethod);
                Optional<MethodInfo>  optionalMethodInfo=commentStore.getMethodInfo(method);
                if(optionalMethodInfo.isPresent()) {
                    List<ParameterInfo> javaParameterInfos=optionalMethodInfo.get().getParameters();
                    
                    if(indexInJavaMethod<javaParameterInfos.size()) {
                        //此处过滤掉comment不为空的参数,是因为对于model中提取出来的参数,已经加上注释了
                        ParameterInfo pi=javaParameterInfos.get(indexInJavaMethod);
                        httpParams.stream()
                          .filter(hp->hp.getComment()==null)
                          .forEach(hp->{
                              String comment=pi.getComment();
                              hp.setComment(comment);
                          });
                        //补全在注解中没有指明名称且参数类型为简单类型的参数的名称
                        if(httpParams.size()==1) {
                            String name=httpParams.get(0).getName();
                            if(StringUtils.isBlank(name)) {
                                httpParams.get(0).setName(pi.getName());
                            }
                        }
                    }  
                }
                httpInterface.httpParameters.addAll(httpParams);
                //根据第一个参数判定的原因是,一个MultipartFile类型的java参数只能解析出一个http请求参数,
                //所以只要判定第一个参数就好了
                HttpParameter firstHttpParameter=httpParams.get(0);
                if (!httpInterface.isMultipart) {
                    httpInterface.isMultipart = TypeUtils.isMultipartType(firstHttpParameter.getJavaType());
                }
                if(!httpInterface.hasRequestBody) {
                    httpInterface.hasRequestBody=!firstHttpParameter.getLocation().isParameter();
                }
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
    private List<HttpParameter> extractHttpParameter(Parameter parameter) {
        
        Optional<HttpParameterExtractor<?>> optionalParameterExtractor= httpParameterProcessors.stream()
            .filter(p->p.accept(parameter))
            .findFirst();
        if(optionalParameterExtractor.isPresent()) {
            return optionalParameterExtractor.get().process(parameter);
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
        JsonFormat jsonFormat=field.getAnnotation(JsonFormat.class);
        if(jsonFormat!=null) {
            return Optional.ofNullable(jsonFormat.pattern());
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
            httpParam.comment=commentStore.getFieldComment(field);
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
