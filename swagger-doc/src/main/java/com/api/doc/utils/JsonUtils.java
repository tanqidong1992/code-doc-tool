package com.api.doc.utils;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;

/**
 * @deprecated use io.swagger.v3.core.util.Json 
 * @author hnoe-dev-tqd
 *
 */
@Deprecated
public class JsonUtils {

	public static String toJson(OpenAPI openAPI) throws JsonProcessingException {
		return Json.pretty(openAPI);
	}
}
