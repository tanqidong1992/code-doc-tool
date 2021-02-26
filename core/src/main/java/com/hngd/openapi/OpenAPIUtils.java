package com.hngd.openapi;

import org.springframework.web.bind.annotation.RequestMethod;

import com.hngd.openapi.entity.HttpInterface;
import com.hngd.openapi.entity.ModuleInfo;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;

public class OpenAPIUtils {

    static String buildOperationId(ModuleInfo moduleInfo, HttpInterface interfaceInfo) {
        String s=moduleInfo.getCanonicalClassName()+"#"+interfaceInfo.getJavaMethodName();
        return s.replaceAll("\\.", "#");
    }

    static void addOpToPath(Operation op, PathItem path, String httpMethod) {
        // GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE
        if (httpMethod.equalsIgnoreCase(RequestMethod.GET.name())) {
            path.setGet(op);
        } else if (httpMethod.equalsIgnoreCase(RequestMethod.HEAD.name())) {
            path.setHead(op);
        } else if (httpMethod.equalsIgnoreCase(RequestMethod.POST.name())) {
            path.setPost(op);
        } else if (httpMethod.equalsIgnoreCase(RequestMethod.PUT.name())) {
            path.setPut(op);
        } else if (httpMethod.equalsIgnoreCase(RequestMethod.PATCH.name())) {
            path.setPatch(op);
        } else if (httpMethod.equalsIgnoreCase(RequestMethod.DELETE.name())) {
            path.setDelete(op);
        } else if (httpMethod.equalsIgnoreCase(RequestMethod.OPTIONS.name())) {
            path.setOptions(op);
        } else if (httpMethod.equalsIgnoreCase(RequestMethod.TRACE.name())) {
            path.setTrace(op);
        }
    }
}
