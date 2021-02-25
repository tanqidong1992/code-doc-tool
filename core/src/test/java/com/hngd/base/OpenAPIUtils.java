package com.hngd.base;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

import org.springframework.beans.BeanUtils;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.parameters.Parameter;

public class OpenAPIUtils {

    public static Optional<Operation> getOperation(OpenAPI openAPI,String path,String httpMethod) {
        PathItem pi=openAPI.getPaths().get(path);
        if(pi==null) {
            return Optional.empty();
        }
        String fieldName=httpMethod.toLowerCase();
        PropertyDescriptor pd=BeanUtils.getPropertyDescriptor(PathItem.class, fieldName);
        if(pd==null) {
            return Optional.empty();
        }
        Operation op=null;
        try {
            op = (Operation) pd.getReadMethod().invoke(pi);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(op);
    }
    
    public static Optional<Parameter> parameterOfOperation(Operation op,String name) {
        return op.getParameters().stream()
            .filter(p->p.getName().equals(name))
            .findFirst();
    }
}
