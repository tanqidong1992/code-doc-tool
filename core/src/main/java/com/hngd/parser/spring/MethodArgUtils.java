package com.hngd.parser.spring;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.Principal;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

public class MethodArgUtils {

	public static Optional<RequestPart> isRequestPart(Annotation[] annotations) {
		return extractAnnotaion(annotations, RequestPart.class);
	}
    /**
     * 提取DataFormatPattern
     * @param annotations
     * @return
     */
	public static Optional<String> extractDateFormat(Annotation[] annotations) {
		Optional<DateTimeFormat> optionalDateTimeFormat=extractAnnotaion(annotations, DateTimeFormat.class);
		if(optionalDateTimeFormat.isPresent()) {
		    return Optional.of(optionalDateTimeFormat.get().pattern());
		}
		return Optional.empty();
	}

	public static boolean isFieldRequired(Field field) {
		return field.getAnnotation(NotNull.class)!=null;
	}
	public static Optional<RequestParam> isRequestParam(Annotation [] annotations) {
		return extractAnnotaion(annotations, RequestParam.class);
	}
	/**
	 * 从注解数组中找到指定类型的注解
	 * @param <T> 指定注解
	 * @param annotations 注解数组
	 * @param type 指定注解的类型
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Annotation> Optional<T> extractAnnotaion(Annotation [] annotations,Class<T> type){
		if(annotations.length<=0) {
			return Optional.empty();
		}
		for (Annotation a : annotations) {
			if(type.isInstance(a)) {
				return Optional.of((T)a);
			}
		}
		return Optional.empty();
	}
	
	public static Optional<RequestBody> isRequestBody(Annotation [] annotations) {
		return extractAnnotaion(annotations, RequestBody.class);
	}
	public static Optional<PathVariable> isPathVariable(Annotation [] annotations) {
		return extractAnnotaion(annotations, PathVariable.class);
	}
	
	public static List<Class<?>> CONTEXT_PARAMTER_TYPES=Arrays.asList(
            //Generic access to request parameters and request and session attributes, without direct use of the Servlet API.
			WebRequest.class,
            NativeWebRequest.class,
            //Choose any specific request or response type — for example, ServletRequest, HttpServletRequest, or Spring’s MultipartRequest, MultipartHttpServletRequest.
            ServletRequest.class,
            ServletResponse.class,
            //Enforces the presence of a session. As a consequence, such an argument is never null. Note that session access is not thread-safe. Consider setting the RequestMappingHandlerAdapter instance’s synchronizeOnSession flag to true if multiple requests are allowed to concurrently access a session.
            HttpSession.class,
            //Servlet 4.0 push builder API for programmatic HTTP/2 resource pushes. Note that, per the Servlet specification, the injected PushBuilder instance can be null if the client does not support that HTTP/2 feature.
            //TODO ?? Servlet 4.0 PushBuilder
            //PushBuilder.class,
            //Currently authenticated user — possibly a specific Principal implementation class if known.
            Principal.class,
            //The HTTP method of the request.
            HttpMethod.class,
            //The current request locale, determined by the most specific LocaleResolver available (in effect, the configured LocaleResolver or LocaleContextResolver).
            Locale.class,
            //The time zone associated with the current request, as determined by a LocaleContextResolver.
            TimeZone.class,
            ZoneId.class,
            //For access to the raw request body as exposed by the Servlet API.
            InputStream.class,
            Reader.class,
            //For access to the raw response body as exposed by the Servlet API.
            OutputStream.class,
            Writer.class,
            //For access to request headers and body. The body is converted with an HttpMessageConverter. See HttpEntity.
            HttpEntity.class,
            //Specify attributes to use in case of a redirect (that is, to be appended to the query string) and flash attributes to be stored temporarily until the request after redirect. See Redirect Attributes and Flash Attributes.
            RedirectAttributes.class,
            /**
             * For access to errors from validation and data binding for a command object (that is, a @ModelAttribute argument) or errors from the validation of a @RequestBody or @RequestPart arguments. You must declare an Errors, or BindingResult argument immediately after the validated method argument.
             */
            Errors.class,
            BindingResult.class,
            //For marking form processing complete, which triggers cleanup of session attributes declared through a class-level @SessionAttributes annotation. See @SessionAttributes for more details.
            SessionStatus.class,
            //For preparing a URL relative to the current request’s host, port, scheme, context path, and the literal part of the servlet mapping. See URI Links.
            UriComponentsBuilder.class
            
			);
	/**
	 * 判定指定参数类型是否是Spring自动注入的参数类型		
	 * @param type 指定参数类型
	 * @return
	 */
	public static boolean isSpringAutoInjectParameterType(Type type) {
		Class<?> paramClass=getRawType(type);
		Optional<Class<?>> innerSuperCalss=CONTEXT_PARAMTER_TYPES.stream()
		    .filter(innerType->innerType.isAssignableFrom(paramClass))
		    .findFirst();
		return innerSuperCalss.isPresent();
	}
	
	public static Class<?> getRawType(Type type){
		Class<?> paramClass;
		if(type instanceof Class<?>) {
			paramClass=(Class<?>)type;
		}else {
			ParameterizedType parameterizedType=(ParameterizedType) type;
			paramClass=(Class<?>)parameterizedType.getRawType();
		}
		return paramClass;
	}
	/**
	 * 判定是否是Map,Model,ModelMap类型的参数
	 * For access to the model that is used in HTML controllers and exposed to templates as part of view rendering.
	 * @param type 参数类型
	 * @return
	 */
	public static boolean isModelParameterType(Type type) {
		Class<?> rawType=getRawType(type);
		return Map.class.isAssignableFrom(rawType) ||
			   Model.class.isAssignableFrom(rawType) ||
			   ModelMap.class.isAssignableFrom(rawType);
	}
	/**
	 * java.util.Map, org.springframework.ui.Model, org.springframework.ui.ModelMap
	 */
}
