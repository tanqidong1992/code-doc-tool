package com.hngd.doc.core.web;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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

public class ModuleParserTest {

	private static final Logger logger=LoggerFactory.getLogger(ModuleParserTest.class);
	@Test
	public void testParse(){
		File f=new File("E:\\Code\\STSCode\\hnvmns-auth\\src\\main\\java\\com\\hngd\\web\\controller\\UserController.java");
		ModuleParserTest.parse(f);
	}

	private static List<ModuleInfo> parse(File f) {
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
		    .filter(ModuleParserTest::isController)
		    .map(ModuleParserTest::parseModule)
		    .collect(Collectors.toList());
	}
	private static boolean isController(ClassOrInterfaceDeclaration clazz){
		List<AnnotationExpr> annotaionExprs=clazz.getAnnotations();
		return annotaionExprs.stream()
		    .filter(ModuleParserTest::isControllerAnnotation)
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
    	Optional<AnnotationExpr> requestMappingAnnotation=getAnnotationByName(clazz.getAnnotations(), "RequestMapping");
    	requestMappingAnnotation.ifPresent(a->moduleInfo.moduleUrl=parseHttpRequestPath(a.getChildrenNodes()));
    	moduleInfo.interfaceInfos=clazz.getChildrenNodes().stream()
    	    .filter(MethodDeclaration.class::isInstance)
		    .map(MethodDeclaration.class::cast)
		    .filter(ModuleParserTest::isInterface)
		    .map(ModuleParserTest::parseInterface)
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
            	    .filter(ModuleParserTest::isHttpRequestParam)
              	    .collect(Collectors.toList());
            	info.isMultipart=httpRequestParameters.stream()
            	    .filter(p->p.getType().toString().equals("MultipartFile"))
            	    .findAny().isPresent();
            	info.parameterInfos=httpRequestParameters.stream()
            	    .map(ModuleParserTest::parseHttpParameterInfo)
            	    .collect(Collectors.toList());
    	}
    	

    	return info;
    	
    }
    private static RequestParameterInfo parseHttpParameterInfo(Parameter parameter){
    	RequestParameterInfo parameterInfo=new RequestParameterInfo();
    	parameterInfo.name=parameter.getType().toString();
    	Optional<AnnotationExpr> pathVariableAnnotation=getAnnotationByName(parameter.getAnnotations(),"PathVariable");
    	parameterInfo.isPathVariable=pathVariableAnnotation.isPresent();
    	parameterInfo.isPrimitive=(parameter.getType() instanceof ReferenceType); 
    	String parameterName=null;
    	if(pathVariableAnnotation.isPresent()){
    		pathVariableAnnotation.get().getChildrenNodes();
    	}
    	parameterInfo.name=parameterName;   
    	return parameterInfo;
    	
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
		    .filter(annotationExpr->ModuleParserTest.isAnnotationNameEqual(annotationExpr,name))
		    .findAny();
    }
	
}
