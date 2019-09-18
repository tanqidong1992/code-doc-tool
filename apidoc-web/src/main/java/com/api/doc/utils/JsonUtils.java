package com.api.doc.utils;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;

public class JsonUtils {

	public static String toJson(OpenAPI openAPI) throws JsonProcessingException {
		return Json.pretty(openAPI);
		//ObjectMapper mapper = new ObjectMapper();
		//mapper.setSerializationInclusion(Include.NON_EMPTY);
		//return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(openAPI);
	}
}
