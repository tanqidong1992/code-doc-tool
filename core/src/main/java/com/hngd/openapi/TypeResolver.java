package com.hngd.openapi;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hngd.parser.source.CommentStore;

import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;

public class TypeResolver {

    private static final Logger logger=LoggerFactory.getLogger(TypeResolver.class);
    
    static Set<Class<?>> resolvedClass = new HashSet<>();
    
    private CommentStore commentStore;
    
    public TypeResolver(CommentStore commentStore) {
        this.commentStore = commentStore;
    }
    private void resolveClassFields(Class<?> clazz, OpenAPI swagger) {
        if(BeanUtils.isSimpleProperty(clazz)) {
            return ;
        }
        if (resolvedClass.contains(clazz)) {
            return;
        }
        resolvedClass.add(clazz);
        Field[] fields = clazz.getDeclaredFields();
        Stream.of(fields)
            .map(Field::getGenericType)
            .filter(filedType->!this.isSimpleType(filedType))
            .forEach(fieldType->resolveAsSchema(fieldType, swagger));
        
    }
     
    private boolean isSimpleType(Type type) {
        return type instanceof Class && BeanUtils.isSimpleProperty((Class<?>)type);
    }
    /**
     * 将类型type解析为schema，并附加到openapi对象上去
     * @param type 类型
     * @param openapi OpenAPI对象
     * @return type对应schema的名称
     */
    @SuppressWarnings({ "rawtypes" })
    public String resolveAsSchema(Type type, OpenAPI openapi) {
        Map<String, Schema> schemas = ModelConverters.getInstance().read(type);
        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            Type[] subTypes = pt.getActualTypeArguments();
            List<String> subTypeSchemaKeys=new ArrayList<>();
            for (Type subType : subTypes) {
                String schemaKey=resolveAsSchema(subType, openapi);
                subTypeSchemaKeys.add(schemaKey);
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
                resolveClassFields(clazz, openapi);
            }
        }
        String firstKey = null;
        for (String key : schemas.keySet()) {
            firstKey = key;
            Schema model = schemas.get(key);
            openapi.schema(key, model);
            attachComment(type, model);
            
        }
        return firstKey;
    }
    @SuppressWarnings("rawtypes")
    private void attachComment(Type type,Schema<?> model) {
        Map<String, Schema> properties = new HashMap<>();
        Map<String,Schema> cps = model.getProperties();
        if (cps == null) {
            return ;
        }
        cps.values().forEach(property -> {
            Schema schema =   property;
            String name = schema.getName();
            Optional<String> propertyComment = getPropertyComment(type, name);
            if (propertyComment.isPresent()) {
                schema.setDescription(propertyComment.get());
            }
            schema.setName(name);
            properties.put(name, schema);
        });
        model.getProperties().clear();
        model.setProperties(properties);
    }
    private Optional<String> getPropertyComment(Type type, String propertyName) {
        Field field = null;
        Class<?> targetClass=null;
        if (type instanceof Class<?>) {
            targetClass=(Class<?>) type;
            field=ReflectionUtils.findField((Class<?>) type,propertyName);
        } else if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            targetClass=(Class<?>) pt.getRawType();
        }
        field=ReflectionUtils.findField(targetClass,propertyName);
        if(field==null) {
            Optional<Field> jsonfield=tryToFindJsonProperty(targetClass, propertyName);
            if(jsonfield.isPresent()) {
                field=jsonfield.get();
            }else {
                logger.error("Counld not found property:{} At Class:{}",propertyName,type.getTypeName());
                return Optional.empty();
            }
        }
        String comment = commentStore.getFieldComment(field);
        return Optional.ofNullable(comment);
    }
    
    public static Optional<Field> tryToFindJsonProperty(Class<?> type, String propertyName) {
        Field[] fields=type.getDeclaredFields();
        for(Field field:fields) {
            JsonProperty jp=field.getAnnotation(JsonProperty.class);
            if(jp!=null && propertyName.equals(jp.value())) {
                return Optional.of(field);
            }
        }
        Class<?> superClass=type.getSuperclass();
        if(superClass==null || Object.class==superClass) {
            return Optional.empty();
        }
        return tryToFindJsonProperty(superClass, propertyName);
    }
}
