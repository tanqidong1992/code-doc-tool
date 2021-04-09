package com.hngd.openapi;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import com.hngd.openapi.entity.HttpParameter;
import com.hngd.utils.TypeUtils;

import io.swagger.v3.core.util.PrimitiveType;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.DateSchema;
import io.swagger.v3.oas.models.media.FileSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;

public class HttpParameterUtils {
 
    public static void resolveParameterInfo(HttpParameter pc, Type parameterType) {
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
                        pc.ref = TypeUtils.toTypeName(argumentType);
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
            ArraySchema arraySchema=new ArraySchema();
            ObjectSchema items=new ObjectSchema();
            arraySchema.setItems(items);
            pc.schema=arraySchema;
            if(pc.ref!=null) {
                resolveRef(pc);
                items.set$ref("#/components/schemas/" + pc.ref);
            }else {
                SimpleTypeFormat tf=SimpleTypeFormat.convert(pc.javaParameterizedType);
                items.setType(tf.getType());
                items.setFormat(tf.getFormat());
            }
        } else if (Number.class.isAssignableFrom(argumentClass) ||
                String.class.isAssignableFrom(argumentClass) ||
                Boolean.class.isAssignableFrom(argumentClass) ) {
            PrimitiveType pt=PrimitiveType.fromType(argumentClass);
            pc.schema = pt.createProperty();
            pc.isPrimitive = true;
        } else if (MultipartFile.class.isAssignableFrom(argumentClass)) {
            //TODO fix it
            pc.isPrimitive = true;
            pc.schema = new FileSchema();
        } else if (Date.class.isAssignableFrom(argumentClass) 
                || LocalDate.class.isAssignableFrom(argumentClass)
                || LocalDateTime.class.isAssignableFrom(argumentClass)) {
            pc.isPrimitive = true;
            DateSchema ds = new DateSchema();
            //TODO fix date format
            ds.setFormat(pc.dateFormat);
            pc.schema = ds;
        }else if(Map.class.isAssignableFrom(argumentClass) || MultiValueMap.class.isAssignableFrom(argumentClass)) {
            pc.isPrimitive = false;
            pc.schema = new ObjectSchema();
        }else{
            pc.isPrimitive = false;
            pc.schema = new ObjectSchema();
            pc.ref = TypeUtils.toTypeName(parameterType);
            resolveRef(pc);
            pc.schema.set$ref("#/components/schemas/" + pc.ref);
        }

    }
    
    private static void resolveRef(HttpParameter pc) {
        if(pc.ref==null) {
            return;
        }
        if (pc.ref.contains("<")) {
            pc.ref = pc.ref.replace("<", "").replace(">", "").replace(",", "");;
        }
    }
}
