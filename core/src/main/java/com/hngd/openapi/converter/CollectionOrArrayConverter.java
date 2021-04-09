package com.hngd.openapi.converter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

import org.springframework.beans.BeanUtils;
import org.springframework.web.multipart.MultipartFile;

import com.hngd.constant.Constants;
import com.hngd.openapi.SimpleTypeFormat;
import com.hngd.openapi.entity.HttpParameter;
import com.hngd.utils.TypeUtils;

import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.FileSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;

public class CollectionOrArrayConverter implements ParameterTypeConverter{

    @Override
    public boolean support(Type type) {
        if (type instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) type).getRawType();
            if (rawType instanceof Class<?>) {
                Class<?> rawClass = (Class<?>) rawType;
                return Collection.class.isAssignableFrom(rawClass);
            }
        }else if(((Class<?>) type).isArray()){
            return true;
        }
        return false;
    }

    @Override
    public void convert(Type type, HttpParameter pc) {
        if(type instanceof ParameterizedType) {
            //TODO fix it
            pc.componentType=(Class<?>)((ParameterizedType) type).getActualTypeArguments()[0];
        }else {
            pc.componentType=((Class<?>)type).getComponentType();
        }
        ArraySchema arraySchema=new ArraySchema();
        ObjectSchema items=new ObjectSchema();
        arraySchema.setItems(items);
        pc.schema=arraySchema;
        if(BeanUtils.isSimpleProperty(pc.componentType)) {
            SimpleTypeFormat tf=SimpleTypeFormat.convert(pc.componentType);
            items.setType(tf.getType());
            items.setFormat(tf.getFormat());
        }else if(MultipartFile.class.isAssignableFrom(pc.componentType)){
            arraySchema.setItems(new FileSchema());
        }else {
            pc.ref = TypeUtils.toRef(pc.componentType);
            items.set$ref(Constants.SCHEMA_REF_PREFIX + pc.ref);
        }
    }

}
