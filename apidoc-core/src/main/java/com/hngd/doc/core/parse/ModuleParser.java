package com.hngd.doc.core.parse;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hngd.doc.core.InterfaceInfo;
import com.hngd.doc.core.InterfaceInfo.RequestParameterInfo;
import com.hngd.doc.core.ModuleInfo;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.Node;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.MemberValuePair;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.StringLiteralExpr;
import japa.parser.ast.type.ReferenceType;
import japa.parser.ast.type.Type;

public class ModuleParser {

	private static final Logger logger=LoggerFactory.getLogger(ModuleParser.class);
 

	public static List<ModuleInfo> parse(File f) {
		CompilationUnit compilationUnit = null;
		try {
			compilationUnit = JavaParser.parse(f);
		} catch (ParseException | IOException e) {
			logger.error("",e);
		}
		if(compilationUnit==null){
			return null;
		}
		List<Node> nodes=compilationUnit.getChildrenNodes();
		return nodes.stream()
		    .filter(ClassOrInterfaceDeclaration.class::isInstance)
		    .map(ClassOrInterfaceDeclaration.class::cast)
		    .filter(ModuleParser::isController)
		    .map(ModuleParser::parseModule)
		    .collect(Collectors.toList());
	}
	private static boolean isController(ClassOrInterfaceDeclaration clazz){
		List<AnnotationExpr> annotaionExprs=clazz.getAnnotations();
		return annotaionExprs.stream()
		    .filter(ModuleParser::isControllerAnnotation)
		    .findAny()
		    .isPresent();
	}
	public static boolean isControllerAnnotation(AnnotationExpr annotationExpr){
		String annotationName=annotationExpr.getName().getName();
		return annotationName.equals("Controller") || annotationName.equals("RestController");
	}
	
	public static boolean isAnnotationNameEqual(AnnotationExpr annotationExpr,String annotationName){
		return annotationExpr.getName().getName().equals(annotationName);
	}
	
    private static ModuleInfo parseModule(ClassOrInterfaceDeclaration clazz){
    	ModuleInfo moduleInfo=new ModuleInfo();
    	moduleInfo.className=clazz.getName();
    	moduleInfo.moduleName=clazz.getName();
    	Optional<AnnotationExpr> requestMappingAnnotation=getAnnotationByName(clazz.getAnnotations(), "RequestMapping");
    	requestMappingAnnotation.ifPresent(a->moduleInfo.moduleUrl=parseHttpRequestPath(a.getChildrenNodes()));
    	moduleInfo.interfaceInfos=clazz.getChildrenNodes().stream()
    	    .filter(MethodDeclaration.class::isInstance)
		    .map(MethodDeclaration.class::cast)
		    .filter(ModuleParser::isInterface)
		    .map(ModuleParser::parseInterface)
		    .collect(Collectors.toList());
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
    private static InterfaceInfo parseInterface(MethodDeclaration method){
    	logger.info("start to parse interface: {}",method.getName());
    	InterfaceInfo info=new InterfaceInfo();
    	info.methodName=method.getName();
    	Optional<HttpRequestInfo> requestInfo=parseHttpRequestInfo(method);
    	if(requestInfo.isPresent()){
    		info.methodUrl=requestInfo.get().path;
    		info.requestType=requestInfo.get().method;
    	}
    	info.retureTypeName=method.getType().toString();
    	List<Parameter> parameters=method.getParameters();
    	
    	if(parameters==null || parameters.size()==0){
    		info.isMultipart=false;
    		info.parameterInfos=Collections.EMPTY_LIST;
    	}else{
        	List<Parameter> httpRequestParameters=parameters.stream()
            	    .filter(ModuleParser::isHttpRequestParam)
              	    .collect(Collectors.toList());
            	info.isMultipart=httpRequestParameters.stream()
            	    .filter(p->p.getType().toString().equals("MultipartFile"))
            	    .findAny().isPresent();
            	info.parameterInfos=httpRequestParameters.stream()
            	    .map(ModuleParser::parseHttpParameterInfo)
            	    .collect(Collectors.toList());
    	}
    	
    	
    	resolvePathVariable(info);
    	

    	return info;
    	
    }
    private static void resolvePathVariable(InterfaceInfo info) {
    	Map<String,Boolean> parameters=info.parameterInfos.stream()
    	.collect(Collectors.toMap(p->p.getName(), p->p.isPrimitive));
		String url=replacePathVariable(info.methodUrl,parameters);
		info.methodUrl=url;
		
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
	private static RequestParameterInfo parseHttpParameterInfo(Parameter parameter){
    	RequestParameterInfo parameterInfo=new RequestParameterInfo();
    	parameterInfo.typeName=parameter.getType().toString();
    	Optional<AnnotationExpr> pathVariableAnnotation=getAnnotationByName(parameter.getAnnotations(),"PathVariable");
    	parameterInfo.isPathVariable=pathVariableAnnotation.isPresent();
    	parameterInfo.isPrimitive=isPrimaryType(parameter.getType()) ;
    	String parameterName=null;
    	if(pathVariableAnnotation.isPresent()){
    		List<Node> nodes=pathVariableAnnotation.get().getChildrenNodes();
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
    	    	List<Node> nodes=requestParamAnnotation.get().getChildrenNodes();
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
    	    		    	    .filter(mvp->mvp.getName().equals("value"))
    	    		    	    .findAny();
    	    		    	if(optionalMvp.isPresent()){
    	    		    		parameterName=((StringLiteralExpr)optionalMvp.get().getValue()).getValue();  
    	    		    	     
    	    		    	}
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
    		List<Node> nodes=requestMapping.get().getChildrenNodes();
    		HttpRequestInfo requestInfo=new HttpRequestInfo();
    		requestInfo.path=parseHttpRequestPath(nodes);
    		requestInfo.method=parseHttpRequestMethod(nodes);
    		if(requestInfo.method==null){
    			requestInfo.method=requestMapping.get().getName().getName().replace("Mapping", "");
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
		    .filter(mvp->mvp.getName().equals("method"))
		    .findFirst();
		if(methodValuePair.isPresent()){
			Expression expression=methodValuePair.get().getValue();
			if(expression instanceof FieldAccessExpr){
				return ((FieldAccessExpr)expression).getField();
			}else if(expression instanceof NameExpr ){
				return ((NameExpr)expression).getName();
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
    	    .filter(mvp->mvp.getName().equals("value") || mvp.getName().equals("path"))
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
		    .filter(annotationExpr->ModuleParser.isAnnotationNameEqual(annotationExpr,name))
		    .findAny();
    }
	
}
