package com.hngd.openapi;

import java.lang.reflect.Type;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import com.hngd.openapi.converter.CollectionOrArrayConverter;
import com.hngd.openapi.converter.DateConverter;
import com.hngd.openapi.converter.MapOrMultiValueMapConverter;
import com.hngd.openapi.converter.MultipartFileConverter;
import com.hngd.openapi.converter.ObjectConverter;
import com.hngd.openapi.converter.ParameterTypeConverter;
import com.hngd.openapi.converter.PrimitiveTypeConverter;
import com.hngd.openapi.entity.HttpParameter;

public class ParameterConverterUtils {
 
    private static final Collection<ParameterTypeConverter> converters;
    static {
        converters=Collections.unmodifiableCollection(
            Arrays.asList(
                new CollectionOrArrayConverter(),
                new DateConverter(),
                new MapOrMultiValueMapConverter(),
                new MultipartFileConverter(),
                new PrimitiveTypeConverter()
                )
       );
        
    }
    private static final ObjectConverter defaultConverter=new ObjectConverter();
    public static void resolveParameterInfo(HttpParameter pc, Type parameterType) {
        converters.stream()
            .filter(c->c.support(parameterType))
            .findFirst()
            .orElseGet(()->defaultConverter)
            .convert(parameterType, pc);
        
    }
}
