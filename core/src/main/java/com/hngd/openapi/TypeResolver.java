package com.hngd.openapi;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
	public void resolveClassFields(Class<?> clazz, OpenAPI swagger) {
		if (resolvedClass.contains(clazz)) {
			return;
		}
		resolvedClass.add(clazz);
		if (!BeanUtils.isSimpleProperty(clazz)) {
			Field[] fields = clazz.getDeclaredFields();
			if (fields != null) {
				for (Field f : fields) {
					Type type = f.getGenericType();
					if(type instanceof Class && BeanUtils.isSimpleProperty((Class<?>)type)) {
						continue;
					}
					resolveType(type, swagger);
				}
			}
		}
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String resolveType(Type type, OpenAPI swagger) {
		Map<String, Schema> schemas = ModelConverters.getInstance().read(type);
		if (type instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) type;
			Type[] subTypes = pt.getActualTypeArguments();
			for (Type subType : subTypes) {
				String tempKey=resolveType(subType, swagger);
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
				resolveClassFields(clazz, swagger);
			}
		}

		String firstKey = null;
		for (String key : schemas.keySet()) {
			firstKey = key;
			Schema model = schemas.get(key);
			swagger.schema(key, model);
			Map<String, Schema> properties = new HashMap<>();
			Map<String, Schema> cps = model.getProperties();
			if (cps == null) {
				continue;
			}
			cps.values().forEach(property -> {
				Schema schema =   property;
				String name = schema.getName();
				if (name.startsWith("get")) {
					name = name.replace("get", "");
				}
				String propertyType=schema.getType();
				String propertyComment = getPropertyComment(type, name);
				if (propertyComment != null) {
					schema.setDescription(propertyComment);
				}
				schema.setName(name);
				properties.put(name, schema);
			});
			model.getProperties().clear();
			model.setProperties(properties);
		}
		return firstKey;
	}
	
	private String getPropertyComment(Type type, String propertyName) {

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
			field=tryToFindJsonProperty(targetClass, propertyName);
			if(field==null) {
				logger.error("Counld not found property:{} At Class:{}",propertyName,type.getTypeName());
				return null;
			}
		}
		String comment = commentStore.getFieldComment(field);
		if (comment != null) {
			return comment;
		} else {
			return null;
		}
	}
	
	public static Field tryToFindJsonProperty(Class<?> type, String propertyName) {
		Field[] fields=type.getDeclaredFields();
		for(Field field:fields) {
			JsonProperty jp=field.getAnnotation(JsonProperty.class);
			if(jp!=null && propertyName.equals(jp.value())) {
				return field;
			}
		}
		Class<?> superClass=type.getSuperclass();
		if(Object.class==superClass) {
			return null;
		}
		return tryToFindJsonProperty(superClass, propertyName);
	}
}
