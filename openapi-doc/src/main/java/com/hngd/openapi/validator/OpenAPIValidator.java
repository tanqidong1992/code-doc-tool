package com.hngd.openapi.validator;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.api.doc.constant.Constant;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ListProcessingReport;
import com.github.fge.jsonschema.core.report.LogLevel;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.core.util.Yaml;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OpenAPIValidator {

	public static final String SCHEMA_PATH = "/com/hngd/openapi/schemas/";
	public static final String SCHEMA_V2 = "schema-v2.json";
	public static final String SCHEMA_V3 = "schema-v3.json";

	public ValidationResponse validate(String s)  {
		return debugByContent(s);
		 
	}

	private JsonSchema getSchemaV3() {

		try (InputStream in = getClass().getResourceAsStream(SCHEMA_PATH + "/" + SCHEMA_V3)) {
			// TODO MAX JSON FILE SIZE
			byte[] buffer = new byte[Constant.MAX_OPENAPI_FILE_SIZE];
			int size = IOUtils.read(in, buffer);
			String schemaStr = new String(buffer, 0, size, Constant.DEFAULT_CHARSET);
			JsonSchema schemaV3 = resolveJsonSchema(schemaStr, true);
			return schemaV3;
		} catch (Exception e) {
			log.error("", e);
		}
		return null;

	}

	static ObjectMapper JsonMapper = Json.mapper();
	static ObjectMapper YamlMapper = Yaml.mapper();

	private JsonSchema resolveJsonSchema(String schemaAsString) throws Exception {
		return resolveJsonSchema(schemaAsString, false);
	}

	private JsonSchema resolveJsonSchema(String schemaAsString, boolean removeId) throws Exception {
		JsonNode schemaObject = JsonMapper.readTree(schemaAsString);
		if (removeId) {
			ObjectNode oNode = (ObjectNode) schemaObject;
			if (oNode.get("id") != null) {
				oNode.remove("id");
			}
			if (oNode.get("$schema") != null) {
				oNode.remove("$schema");
			}
			if (oNode.get("description") != null) {
				oNode.remove("description");
			}
		}
		JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
		return factory.getJsonSchema(schemaObject);

	}

	private ValidationResponse debugByContent(String s){
		ValidationResponse output=new ValidationResponse();
		JsonNode spec=null;
		try {
			spec = JsonMapper.readTree(s);
		} catch (JsonProcessingException e) {
			log.error("",e);
		}
		if (spec == null) {
			ProcessingMessage pm = new ProcessingMessage();
			pm.setLogLevel(LogLevel.ERROR);
			pm.setMessage("Unable to read content.  It may be invalid JSON or YAML");
			output.addValidationMessage(new SchemaValidationError(pm.asJson()));
			return output;
		}
		// do actual JSON schema validation
		JsonSchema schema = getSchemaV3();
		ProcessingReport report=null;
		try {
			report = schema.validate(spec);
		} catch (ProcessingException e) {
			log.error("",e);
		}
		ListProcessingReport lp = new ListProcessingReport();
		try {
			lp.mergeWith(report);
		} catch (ProcessingException e) {
			log.error("",e);
		}
		java.util.Iterator<ProcessingMessage> it = lp.iterator();
		while (it.hasNext()) {
			ProcessingMessage pm = it.next();
			output.addValidationMessage(new SchemaValidationError(pm.asJson()));
		}
		return output;
	}

}
