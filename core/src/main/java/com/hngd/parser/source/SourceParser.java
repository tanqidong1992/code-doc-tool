package com.hngd.parser.source;


import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;

import com.hngd.openapi.entity.HttpInterface;
import com.hngd.openapi.entity.HttpParameter;
import com.hngd.openapi.entity.ModuleInfo;
import com.hngd.utils.ClassUtils;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.type.Type;

@Deprecated
public class SourceParser {

	private static final Logger logger=LoggerFactory.getLogger(SourceParser.class);
 

	public static List<ModuleInfo> parse(File f) {
		 CompilationUnit compilationUnit = ClassUtils.parseClass(f);
		if(compilationUnit==null){
			return null;
		}
		List<Node> nodes=compilationUnit.getChildNodes();
		return nodes.stream()
		    .filter(ClassOrInterfaceDeclaration.class::isInstance)
		    .map(ClassOrInterfaceDeclaration.class::cast)
		    .filter(SourceParser::isController)
		    .map(SourceParser::parseModule)
		    .collect(Collectors.toList());
	}
	private static boolean isController(ClassOrInterfaceDeclaration clazz){
		List<AnnotationExpr> annotaionExprs=clazz.getAnnotations();
		if(CollectionUtils.isEmpty(annotaionExprs)){
			return false;
		}
		return annotaionExprs.stream()
		    .filter(SourceParser::isControllerAnnotation)
		    .findAny()
		    .isPresent();
	}
	public static boolean isControllerAnnotation(AnnotationExpr annotationExpr){
		String annotationName=annotationExpr.getName().asString();
		return annotationName.equals("Controller") || annotationName.equals("RestController");
	}
	
	public static boolean isAnnotationNameEqual(AnnotationExpr annotationExpr,String annotationName){
		return annotationExpr.getNameAsString().equals(annotationName);
	}
	
    private static ModuleInfo parseModule(ClassOrInterfaceDeclaration clazz){
    	ModuleInfo moduleInfo=new ModuleInfo();
    	moduleInfo.setSimpleClassName(clazz.getName().asString());
    	moduleInfo.setName(clazz.getName().asString());
    	Optional<AnnotationExpr> requestMappingAnnotation=getAnnotationByName(clazz.getAnnotations(), "RequestMapping");
    	requestMappingAnnotation.ifPresent(a->moduleInfo.setUrl(parseHttpRequestPath(a.getChildNodes())));
    	List<HttpInterface> interfaceInfos=clazz.getChildNodes().stream()
    	    .filter(MethodDeclaration.class::isInstance)
		    .map(MethodDeclaration.class::cast)
		    .filter(SourceParser::isInterface)
		    .map(SourceParser::parseInterface)
		    .collect(Collectors.toList());
    	moduleInfo.setInterfaceInfos(interfaceInfos);
    	return moduleInfo;	
	}
    private static boolean isInterface(MethodDeclaration method){
    	List<AnnotationExpr> annotationExprs=method.getAnnotations();
    	if(annotationExprs==null ||annotationExprs.size()==0){
    		logger.warn("there is no requestmapping annotation for method {}",method.toString());
    		return false;
    	}
    	return requestMappingAnnotationNames.stream()
    	    .map(requestMappingAnnotationName->getAnnotationByName(annotationExprs,requestMappingAnnotationName))
    	    .filter(Optional::isPresent)
    		.findAny()
    		.isPresent();	
    }
    private static HttpInterface parseInterface(MethodDeclaration method){
    	logger.info("start to parse interface: {}",method.getName());
    	HttpInterface info=new HttpInterface();
    	info.javaMethodName=method.getName().asString();
    	Optional<HttpRequestInfo> requestInfo=parseHttpRequestInfo(method);
    	if(requestInfo.isPresent()){
    		info.url=requestInfo.get().path;
    		info.httpMethod=requestInfo.get().method;
    	}
    	info.javaReturnTypeName=method.getType().toString();
    	List<Parameter> parameters=method.getParameters();
    	if(parameters==null || parameters.size()==0){
    		info.isMultipart=false;
    		info.httpParameters=Collections.emptyList();
    	}else{
        	List<Parameter> httpRequestParameters=parameters.stream()
            	    .filter(SourceParser::isHttpRequestParam)
              	    .collect(Collectors.toList());
        	if(CollectionUtils.isEmpty(httpRequestParameters)) {
        		List<Parameter> requestBodies=parameters.stream()
        		 .filter(SourceParser::isHttpRequestBody)
        		 .collect(Collectors.toList());
        		  info.hasRequestBody=true;
        		  info.setConsumes(Arrays.asList(MediaType.APPLICATION_JSON_VALUE));
        		  info.httpParameters=requestBodies.stream()
        	            	.map(SourceParser::parseHttpParameterInfo)
        	            	.collect(Collectors.toList());
        		  
        	}else {
        		//info.setConsumes(Arrays.asList(MediaType.APPLICATION_FORM_URLENCODED_VALUE));
        		 info.httpParameters=httpRequestParameters.stream()
        	            	.map(SourceParser::parseHttpParameterInfo)
        	            	.collect(Collectors.toList());
        	}
            info.isMultipart=httpRequestParameters.stream()
            	.filter(p->isMultipart(p))
            	.findAny().isPresent();
           
    	}
    	resolvePathVariable(info);
    	return info;
    }
    private static boolean isMultipart(Parameter p) {
    	if(p.getType().toString().equals("MultipartFile")) {
    		return true;
    	}
    	
    	return false;
    	
    }
    private static void resolvePathVariable(HttpInterface info) {
    	System.out.println(info.getJavaMethodName());
    	Map<String,Boolean> parameters=info.httpParameters.stream()
    	.collect(Collectors.toMap(p->p.getName(), p->p.isPrimitive));
		String url=replacePathVariable(info.url,parameters);
		info.url=url;
		
	}
	private static String replacePathVariable(String methodUrl, Map<String, Boolean> parameters) {
		
		String newUrl=new String(methodUrl);
        Pattern pattern=Pattern.compile("\\{[^\\{\\}]+\\}");
		Matcher matcher=pattern.matcher(methodUrl);
		while(matcher.find()){
			int start=matcher.start();
		    int end=matcher.end();
		    String toReplaceStr=methodUrl.substring(start, end);
		    String pathVariableName=toReplaceStr.substring(1, toReplaceStr.length()-1);
		    Boolean isPrimary=parameters.get(pathVariableName);
		    if(isPrimary){
		    	pathVariableName=pathVariableName+"Str";
		    }else{
		    	
		    }
		    newUrl=newUrl.replace(toReplaceStr, "\"+"+pathVariableName+"+\"");
		}
		return newUrl;
	}
	private static HttpParameter parseHttpParameterInfo(Parameter parameter){
    	HttpParameter parameterInfo=new HttpParameter();
    	parameterInfo.javaTypeName=parameter.getType().getParentNode().toString();//.toString();
    	Optional<AnnotationExpr> pathVariableAnnotation=getAnnotationByName(parameter.getAnnotations(),"PathVariable");
    	parameterInfo.isPathVariable=pathVariableAnnotation.isPresent();
    	parameterInfo.isPrimitive=isPrimaryType(parameter.getType()) ;
    	String parameterName=null;
    	if(pathVariableAnnotation.isPresent()){
    		List<Node> nodes=pathVariableAnnotation.get().getChildNodes();
    		Optional<StringLiteralExpr> optionStr=nodes.stream()
    		    .filter(StringLiteralExpr.class::isInstance)
    		    .map(StringLiteralExpr.class::cast)
    		    .findAny();
    		if(optionStr.isPresent()){
    			parameterName=optionStr.get().getValue();
    		}
    		 
    	}else{
    	    Optional<AnnotationExpr> requestParamAnnotation=getAnnotationByName(parameter.getAnnotations(),"RequestParam");
    	    if(requestParamAnnotation.isPresent()){
    	    	List<Node> nodes=requestParamAnnotation.get().getChildNodes();
    	    	Optional<StringLiteralExpr> optionStr=nodes.stream()
    	    		    .filter(StringLiteralExpr.class::isInstance)
    	    		    .map(StringLiteralExpr.class::cast)
    	    		    .findAny();
    	    		if(optionStr.isPresent()){
    	    			parameterName=optionStr.get().getValue();
    	    		}else{
    	    			Optional<MemberValuePair> optionalMvp= nodes.stream()
    	    		    	    .filter(MemberValuePair.class::isInstance)
    	    		    	    .map(MemberValuePair.class::cast)
    	    		    	    .filter(mvp->mvp.getNameAsString().equals("value"))
    	    		    	    .findAny();
    	    		    	if(optionalMvp.isPresent()){
    	    		    		parameterName=((StringLiteralExpr)optionalMvp.get().getValue()).getValue();  
    	    		    	     
    	    		    	}
    	    		}
    	    }else {
    	    	
    	    	Optional<AnnotationExpr> requestBodyAnnotation=getAnnotationByName(parameter.getAnnotations(),"RequestBody");
        	    if(requestBodyAnnotation.isPresent()) {
        	    	parameterName="body";
        	    }
    	    }
    	}
    	parameterInfo.name=parameterName;   
    	return parameterInfo;
    	
    }
    public static final List<String> PRIMARY_TYPES=Arrays.asList("String",
    		"byte","Byte",
    		"short","Short",
    		"int","Integer",
    		"long","Long",
    		"float","Float",
    		"double","Double",
    		"char","Character",
    		"boolean","Boolean");
    private static boolean isPrimaryType(Type type) {
		String typeName=type.toString();
		
		return PRIMARY_TYPES.contains(typeName);
	}
    private static boolean isHttpRequestBody(Parameter parameter){
    	//logger.info("parse parameter:{}",parameter.toString());
    	List<AnnotationExpr> annotationExprs=parameter.getAnnotations();
    	if(annotationExprs==null || annotationExprs.size()==0){
    		logger.warn("there is no RequestBody annotation for parameter {}",parameter.toString());
    		return false;
    	}
    	return getAnnotationByName(annotationExprs, "RequestBody").isPresent();
    	    
    	 
    }
    private static boolean isHttpRequestParam(Parameter parameter){
    	//logger.info("parse parameter:{}",parameter.toString());
    	List<AnnotationExpr> annotationExprs=parameter.getAnnotations();
    	if(annotationExprs==null || annotationExprs.size()==0){
    		logger.warn("there is no httprequestparam annotation for parameter {}",parameter.toString());
    		return false;
    	}
    	return httpParameterAnnotationNames.stream()
    	    .map(paramAnnotationName->getAnnotationByName(annotationExprs, paramAnnotationName))
    	    .filter(Optional::isPresent)
    	    .findAny()
    	    .isPresent();
    	 
    }
    private static Optional<HttpRequestInfo> parseHttpRequestInfo(MethodDeclaration method) {
    	List<AnnotationExpr> annotationExprs=method.getAnnotations();
    	Optional<AnnotationExpr> requestMapping=getRequestMappingAnnotation(annotationExprs);
    	if(requestMapping.isPresent()){
    		List<Node> nodes=requestMapping.get().getChildNodes();
    		HttpRequestInfo requestInfo=new HttpRequestInfo();
    		requestInfo.path=parseHttpRequestPath(nodes);
    		requestInfo.method=parseHttpRequestMethod(nodes);
    		if(requestInfo.method==null){
    			requestInfo.method=requestMapping.get().getName().asString().replace("Mapping", "");
    		}
    		//TODO parse more http request info
    		return Optional.of(requestInfo);
    	}
    	return Optional.empty();
	}
 
    private static String parseHttpRequestMethod(List<Node> nodes) {
    	Optional<MemberValuePair> methodValuePair=nodes.stream()
		    .filter(MemberValuePair.class::isInstance)
		    .map(MemberValuePair.class::cast)
		    .filter(mvp->mvp.getName().asString().equals("method"))
		    .findFirst();
		if(methodValuePair.isPresent()){
			Expression expression=methodValuePair.get().getValue();
			if(expression instanceof FieldAccessExpr){
				return ((FieldAccessExpr)expression).getNameAsString();
			}else if(expression instanceof NameExpr ){
				return ((NameExpr)expression).getNameAsString();
			}
			return expression.toString();
		}
		return null;
	}

	private static String parseHttpRequestPath(List<Node> nodes) {
    	Optional<StringLiteralExpr> optionalStringLiteralExpr=nodes.stream()
    	    .filter(StringLiteralExpr.class::isInstance)
    	    .map(StringLiteralExpr.class::cast)
    	    .findAny();
    	if(optionalStringLiteralExpr.isPresent()){
    	    String path=optionalStringLiteralExpr.get().getValue();
    	    return path;
    	}
    	Optional<MemberValuePair> optionalMvp= nodes.stream()
    	    .filter(MemberValuePair.class::isInstance)
    	    .map(MemberValuePair.class::cast)
    	    .filter(mvp->mvp.getNameAsString().equals("value") || mvp.getNameAsString().equals("path"))
    	    .findAny();
    	if(optionalMvp.isPresent()){
    	    String path=((StringLiteralExpr)optionalMvp.get().getValue()).getValue();  
    	    return path;
    	}
    	return null;    	 
	}

	static class HttpRequestInfo{
    	String path;
    	String method;
    	String[] consumes;
    	String[] produces;
    	String[] headers;
    	String[] params;
    	String name;
    	
    }
	public static final List<String> httpParameterAnnotationNames=Arrays.asList(
    		"PathVariable",
    		"RequestParam");
	
    public static final List<String> requestMappingAnnotationNames=Arrays.asList(
    		"GetMapping",
    		"PostMapping",
    		"PutMapping",
    		"DeleteMapping",
    		"PatchMapping",
    		"RequestMapping");
	private static Optional<AnnotationExpr> getRequestMappingAnnotation(List<AnnotationExpr> annotationExprs) {
		return requestMappingAnnotationNames.stream()
		    .map(requestMappingAnnotationName->getAnnotationByName(annotationExprs,requestMappingAnnotationName))
		    .filter(Optional::isPresent)
		    .map(Optional::get)
		    .findFirst();
	}

    private static Optional<AnnotationExpr> getAnnotationByName(List<AnnotationExpr> annotationExprs,String name){
        return annotationExprs.stream()
		    .filter(annotationExpr->SourceParser.isAnnotationNameEqual(annotationExpr,name))
		    .findAny();
    }
	
}
