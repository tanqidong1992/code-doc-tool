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
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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
public class SwaggerDocGenerator
{
    static List<String>         application_json       = Arrays.asList("application/json", "*");
    static List<String>         application_url_encode = Arrays.asList("application/json");
    private static final Logger logger                 = Logger.getLogger(SwaggerDocGenerator.class);
    Swagger                     mSwagger;

    public SwaggerDocGenerator(Swagger mSwagger)
    {
        this.mSwagger = mSwagger;
    }

    public static List<Class<?>> getPacakge(String packageName)
    {
        String packagePath = packageName.replaceAll("\\.", "/");
        Enumeration<URL> dirs = null;
        try
        {
            dirs = SwaggerDocGenerator.class.getClassLoader().getResources(packagePath);
        } catch (IOException e)
        {
            logger.error("", e);
        }
        List<Class<?>> clazzs = new LinkedList<>();
        while (dirs.hasMoreElements())
        {
            URL url = dirs.nextElement();
            File file = new File(url.getFile());
            // 把此目录下的所有文件列出
            String[] classes = file.list();
            // 循环此数组，并把.class去掉
            for (String className : classes)
            {
                if (!className.endsWith(".class"))
                {
                    logger.error("the file[" + file.getAbsolutePath() + className + "] is not a class");
                    continue;
                }
                // logger.info("resolve class["+className+"]");
                className = className.substring(0, className.length() - 6);
                // 拼接上包名，变成全限定名
                String qName = packageName + "." + className;
                // 去掉Mybatis生成的Example类
                if (qName.endsWith("Example"))
                {
                    continue;
                }
                // 如有需要，把每个类生实一个实例
                Class<?> cls = null;
                try
                {
                    cls = Class.forName(qName);
                } catch (ClassNotFoundException e)
                {
                    logger.error("", e);
                }
                if (cls != null)
                {
                    clazzs.add(cls);
                }
            }
        }
        return clazzs;
    }

    public void parse(String packageName)
    {
        List<Class<?>> clses = getPacakge(packageName);
        parse(clses);
    }

    public void parse(List<Class<?>> clses)
    {
        List<Tag> tags = new ArrayList<Tag>();
        Map<String, Path> paths = new HashMap<String, Path>();
        mSwagger.setPaths(paths);
        mSwagger.setTags(tags);
        for (Class<?> cls : clses)
        {
            ModuleInfo mi = processClass(cls);
            if (mi != null)
            {
                String c = ControllerClassCommentParser.classComments.get(mi.className);
                Tag commentTag = null;
                if (c != null)
                {
                    commentTag = new Tag();
                    commentTag.setName(c);
                    tags.add(commentTag);
                } else
                {
                    logger.warn("the comment for class[" + mi.className + "] is empty");
                }
                for (InterfaceInfo ii : mi.interfaceInfos)
                {
                    String methodKey = cls.getSimpleName() + "#" + ii.methodName;
                    MethodInfo mic = ControllerClassCommentParser.methodComments.get(methodKey);
                    if (mic == null || mic.parameters == null)
                    {
                        logger.error("the method comment for method[" + methodKey + "] is empty");
                        continue;
                    }
                    String pathStr = mi.moduleUrl + ii.methodUrl;
                    Path path = new Path();
                    Operation op = new Operation();
                    List<String> operationTags = new ArrayList<>();
                    if (mic.createTimeStr != null)
                    {
                        operationTags.add(mic.createTimeStr);
                    }
                    if (commentTag != null)
                    {
                        operationTags.add(commentTag.getName());
                    }
                    op.setTags(operationTags);
                    op.setConsumes(CollectionUtils.isEmpty(ii.consumes) ? application_url_encode : ii.consumes);
                    op.setProduces(CollectionUtils.isEmpty(ii.produces) ? application_json : ii.produces);
                    op.setOperationId(ii.methodName);
                    if (mic != null)
                    {
                        op.setDescription(mic.comment);
                    }
                    if(ii.parameterNames.size()>mic.parameters.size()){
                    	continue;
                    }
                    List<io.swagger.models.parameters.Parameter> parameters = new ArrayList<io.swagger.models.parameters.Parameter>();
                    for (int i = 0; i < ii.parameterNames.size(); i++)
                    {
                        RequestParameterInfo rpi = ii.parameterNames.get(i);
                        Type pt = ii.parameterTypes.get(i);
                        if (i >= mic.parameters.size())
                        {
                            logger.error(mi.moduleName + "." + ii.methodName + "." + rpi);
                        }
                        ParameterInfo pc = mic.parameters.get(i);
                        Type parameterType = ii.parameterTypes.get(i);
                        if (!isPrimitiveType(parameterType))
                        {
                            pc.ref = TypeNameUtils.getTypeName(parameterType);
                            if (pc.ref.contains("<"))
                            {
                                pc.ref = pc.ref.replace("<", "").replace(">", "");
                            }
                            if (parameterType instanceof ParameterizedType)
                            {
                                ParameterizedType ppt = (ParameterizedType) parameterType;
                                Type rawType = ppt.getRawType();
                                if (rawType instanceof Class<?>)
                                {
                                    Class<?> rawClass = (Class<?>) rawType;
                                    if (Collection.class.isAssignableFrom(rawClass))
                                    {
                                        pc.isCollection = true;
                                        Type[] argumentTypes = ppt.getActualTypeArguments();
                                        if (argumentTypes != null && argumentTypes.length > 0)
                                        {
                                            Type argumentType0 = argumentTypes[0];
                                            Class<?> argumentClass = (Class<?>) argumentType0;
                                            if (String.class.isAssignableFrom(argumentClass))
                                            {
                                                pc.format = "string";
                                                pc.type = "string";
                                                pc.isArgumentTypePrimitive = true;
                                            } else if (Number.class.isAssignableFrom(argumentClass))
                                            {
                                                pc.format = "Number";
                                                pc.type = argumentClass.getSimpleName();
                                                pc.isArgumentTypePrimitive = true;
                                            } else if (Boolean.class.isAssignableFrom(argumentClass))
                                            {
                                                pc.format = "Boolean";
                                                pc.type = argumentClass.getSimpleName();
                                                pc.isArgumentTypePrimitive = true;
                                            } else
                                            {
                                                pc.format = "Object";
                                                pc.type = argumentClass.getSimpleName();
                                                pc.isArgumentTypePrimitive = false;
                                            }
                                        }
                                    }
                                } else
                                {
                                }
                            }
                        } else
                        {
                            Class<?> argumentClass = (Class<?>) parameterType;
                            if (String.class.isAssignableFrom(argumentClass))
                            {
                                pc.format = "string";
                                pc.type = "string";
                                pc.isArgumentTypePrimitive = true;
                            } else if (Number.class.isAssignableFrom(argumentClass))
                            {
                                pc.type = "number";
                                pc.format = argumentClass.getSimpleName().toLowerCase();
                                pc.isArgumentTypePrimitive = true;
                            } else if (Boolean.class.isAssignableFrom(argumentClass))
                            {
                                pc.type = "boolean";
                                pc.format = argumentClass.getSimpleName().toLowerCase();
                                pc.isArgumentTypePrimitive = true;
                            } else
                            {
                                pc.type = "object";
                                pc.format = argumentClass.getSimpleName();
                                pc.isArgumentTypePrimitive = false;
                            }
                        }
                        resolveType(parameterType, mSwagger);
                        if (ii.requestType.equals(RequestMethod.GET.name()))
                        {
                            if (rpi.paramType.equals(ParamType.REQUEST))
                            {
                                if (pc.ref != null)
                                {
                                    BodyParameter bp = new BodyParameter();
                                    bp.setDescription(pc.comment);
                                    bp.setIn("query");
                                    bp.setName(rpi.name);
                                    bp.setRequired(rpi.required);
                                    Model schema = null;
                                    if (pc.isCollection)
                                    {
                                        ArrayModel am = new ArrayModel();
                                        am.setType("array");
                                        AbstractProperty items = new RefProperty();
                                        if (pc.isArgumentTypePrimitive)
                                        {
                                            items = new ArrayProperty();
                                            items.setFormat(pc.format);
                                        } else
                                        {
                                            ((RefProperty) items).set$ref("#/definitions/" + pc.type);
                                            // am.setReference("#/definitions/"+pc.type);
                                        }
                                        items.setType(pc.type);
                                        am.setItems(items);
                                        schema = am;
                                    } else
                                    {
                                        schema = new RefModel("#/definitions/" + pc.ref);
                                    }
                                    bp.setSchema(schema);
                                    parameters.add(bp);
                                } else
                                {
                                    QueryParameter param = new QueryParameter();
                                    param.setIn("query");
                                    param.setName(rpi.name);
                                    param.setRequired(rpi.required);
                                    param.setType(pc.type);
                                    param.setFormat(pc.format);
                                    param.setDescription(pc.comment);
                                    parameters.add(param);
                                }
                            } else if (rpi.paramType.equals(ParamType.PATH))
                            {
                                PathParameter pathParameter = new PathParameter();
                                pathParameter.setIn("path");
                                pathParameter.setName(rpi.name);
                                pathParameter.setRequired(rpi.required);
                                pathParameter.setType(pc.type);
                                pathParameter.setFormat(pc.format);
                                pathParameter.setDescription(pc.comment);
                                parameters.add(pathParameter);
                            }
                        } else
                        {
                            if (rpi.paramType.equals(ParamType.REQUEST))
                            {
                                if (pc.ref != null)
                                {
                                    BodyParameter bp = new BodyParameter();
                                    bp.setDescription(pc.comment);
                                    bp.setIn("body");
                                    bp.setName(rpi.name);
                                    bp.setRequired(rpi.required);
                                    Model schema = null;
                                    if (pc.isCollection)
                                    {
                                        ArrayModel am = new ArrayModel();
                                        am.setType("array");
                                        AbstractProperty items = new RefProperty();
                                        if (pc.isArgumentTypePrimitive)
                                        {
                                            items = new ArrayProperty();
                                            items.setFormat(pc.format);
                                        } else
                                        {
                                            ((RefProperty) items).set$ref("#/definitions/" + pc.type);
                                            // am.setReference("#/definitions/"+pc.type);
                                        }
                                        items.setType(pc.type);
                                        items.setFormat(pc.format);
                                        am.setItems(items);
                                        schema = am;
                                    } else
                                    {
                                        schema = new RefModel("#/definitions/" + pc.ref);
                                    }
                                    bp.setSchema(schema);
                                    parameters.add(bp);
                                } else
                                {
                                    FormParameter param = new FormParameter();
                                    param.setIn("body");
                                    param.setName(rpi.name);
                                    param.setRequired(rpi.required);
                                    param.setType(pc.type);
                                    param.setDescription(pc.comment);
                                    param.setFormat(pc.format);
                                    parameters.add(param);
                                }
                            } else if (rpi.paramType.equals(ParamType.PATH))
                            {
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
                    if (mic != null)
                    {
                        resp.setDescription(mic.retComment);
                    }
                    String firstKey = resolveType(ii.retureType, mSwagger);
                    RefProperty schema = new RefProperty();
                    schema.set$ref("#/definitions/" + firstKey);
                    resp.setSchema(schema);
                    resp.setDescription("test");
                    responses.put("200", resp);
                    op.setResponses(responses);
                    path.set(ii.requestType.toLowerCase(), op);
                    paths.put(pathStr, path);
                }
            }
        }
    }

    public static ModuleInfo processClass(Class<?> cls)
    {
        ModuleInfo mi = new ModuleInfo();
        Controller controller = cls.getAnnotation(Controller.class);
        if (controller == null)
        {
            logger.warn("class[" + cls.getName() + "] has no annotation[" + Controller.class.getName() + "]");
            return null;
        }
        RequestMapping requestMapping = cls.getAnnotation(RequestMapping.class);
        if (requestMapping == null || requestMapping.value().length <= 0)
        {
            logger.warn("class[" + cls.getName() + "] has no annotation[" + RequestMapping.class.getName() + "]");
            return null;
        }
        mi.moduleUrl = requestMapping.value()[0];
        if (!StringUtils.startsWith(mi.moduleUrl, "/"))
        {
            mi.moduleUrl = "/" + mi.moduleUrl;
        }
        mi.moduleName = mi.moduleUrl.substring(1);
        mi.className = cls.getSimpleName();
        RequestMethod[] requestMethods = requestMapping.method();
        RequestMethod requestMethod = null;
        if (requestMethods.length > 0)
        {
            requestMethod = requestMethods[0];
        }
        // TODO Auto-generated method stub
        Method[] methods = cls.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++)
        {
            Method method = methods[i];
            RequestMapping rm = method.getAnnotation(RequestMapping.class);
            if (rm != null)
            {
                InterfaceInfo info = processMethod(method);
                if (info != null)
                {
                    mi.interfaceInfos.add(info);
                }
            } else
            {
                logger.warn(
                        "method[" + method.getName() + "] has no Annotation[" + RequestMapping.class.getName() + "]");
            }
        }
        return mi;
    }

    private static InterfaceInfo processMethod(Method method)
    {
        InterfaceInfo info = new InterfaceInfo();
        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
        if (requestMapping == null)
        {
            return null;
        }
        String[] consumes = requestMapping.consumes();
        if (consumes != null && consumes.length > 0)
        {
            info.consumes = new ArrayList<>(Arrays.asList(consumes));
        }
        String[] produces = requestMapping.produces();
        if (produces != null && produces.length > 0)
        {
            info.produces = new ArrayList<>(Arrays.asList(produces));
        }
        info.methodUrl = requestMapping.value()[0];
        if (!StringUtils.startsWith(info.methodUrl, "/"))
        {
            info.methodUrl = "/" + info.methodUrl;
        }
        info.retureTypeName = method.getReturnType().getSimpleName();
        info.retureType = method.getReturnType();
        info.methodName = method.getName();
        info.retureType = method.getGenericReturnType();
        info.requestType = requestMapping.method()[0].name();
        Annotation[][] annotationss = method.getParameterAnnotations();
        Parameter[] parameters = method.getParameters();
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < parameters.length; i++)
        {
            // System.out.println(parameterTypes[i].getName());
            Parameter parameter = parameters[i];
            Annotation[] annotations = parameter.getAnnotations();
            if (annotations.length > 0)
            {
                for (Annotation a : annotations)
                {
                    if (a instanceof RequestParam)
                    {
                        RequestParam requestParam = (RequestParam) a;
                        RequestParameterInfo rpi = new RequestParameterInfo();
                        rpi.name = requestParam.value();
                        rpi.paramType = ParamType.REQUEST;
                        rpi.required = requestParam.required();
                        info.parameterNames.add(rpi);
                        info.parameterTypes.add(parameter.getParameterizedType());
                        if (!info.isMultipart)
                        {
                            info.isMultipart = TypeUtils.isMultipartType(parameter.getParameterizedType());
                        }
                        break;
                    } else if (a instanceof PathVariable)
                    {
                        PathVariable pa = (PathVariable) a;
                        RequestParameterInfo rpi = new RequestParameterInfo();
                        rpi.name = pa.value();
                        rpi.paramType = ParamType.PATH;
                        rpi.required = true;
                        info.parameterNames.add(rpi);
                        if (!info.isMultipart)
                        {
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

    public static String resolveType(Type type, Swagger swagger)
    {
        Map<String, Model> models = ModelConverters.getInstance().read(type);
        if (type instanceof ParameterizedType)
        {
            ParameterizedType pt = (ParameterizedType) type;
            Type[] subTypes = pt.getActualTypeArguments();
            for (Type subType : subTypes)
            {
                resolveType(subType, swagger);
            }
        }
        String firstKey = null;
        for (String key : models.keySet())
        {
            firstKey = key;
            Model model = models.get(key);
            Map<String, Property> properties = new HashMap<String, Property>();
            Map<String, Property> cps = model.getProperties();
            if (cps != null)
            {
                model.getProperties().values().forEach(property ->
                {
                    String name = property.getName();
                    if (name.startsWith("get"))
                    {
                        name = name.replace("get", "");
                    }
                    String fcKey = null;
                    if (type instanceof Class<?>)
                    {
                        String typeName = ((Class<?>) type).getSimpleName();
                        fcKey = typeName + "#" + name;
                    } else if (type instanceof ParameterizedType)
                    {
                        ParameterizedType pt = (ParameterizedType) type;
                        // fcKey=name;
                        String typeName = ((Class<?>) pt.getRawType()).getSimpleName();
                        fcKey = typeName + "#" + name;
                    }
                    FieldInfo fi = EntityClassCommentParser.fieldComments.get(fcKey);
                    if (fi != null)
                    {
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

    private static boolean isPrimitiveType(Type parameterType)
    {
        if (!(parameterType instanceof Class<?>))
        {
            return false;
        }
        if (String.class.equals(parameterType))
        {
            return true;
        }
        Class<?> cls = (Class<?>) parameterType;
        if (Number.class.isAssignableFrom(cls))
        {
            return true;
        }
        if (Boolean.class.isAssignableFrom(cls))
        {
            return true;
        }
        return false;
    }
}
