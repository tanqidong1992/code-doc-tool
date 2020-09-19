package com.hngd.openapi.validator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

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
import com.hngd.openapi.constant.Constant;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OpenAPIValidator {

	public static final String SCHEMA_PATH = "/com/hngd/openapi/schemas/";
	public static final String SCHEMA_V2 = "schema-v2.json";
	public static final String SCHEMA_V3 = "schema-v3.json";
	public ValidationResponse validate(File file) throws IOException  {
		String s=FileUtils.readFileToString(file, Constant.DEFAULT_CHARSET);
		return debugByContent(s);
		 
	}
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
		JsonNode spec = readNode(s);
		 
		if (spec == null) {
			ProcessingMessage pm = new ProcessingMessage();
			pm.setLogLevel(LogLevel.ERROR);
			pm.setMessage("Unable to read content.  It may be invalid JSON or YAML");
			output.addValidationMessage(new SchemaValidationError(pm.asJson()));
			return output;
		}
		
		
		 SwaggerParseResult result = null;
         try {
             result = readOpenApi(s);
         } catch (Exception e) {
             log.error("can't read OpenAPI contents", e);
             ProcessingMessage pm = new ProcessingMessage();
             pm.setLogLevel(LogLevel.ERROR);
             pm.setMessage("unable to parse OpenAPI: " + e.getMessage());
             output.addValidationMessage(new SchemaValidationError(pm.asJson()));
             return output;
         }
         if (result != null) {
             for (String message : result.getMessages()) {
                 output.addMessage(message);
             }
         }
		
		// do actual JSON schema validation
		JsonSchema schema = getSchemaV3();
		
		ProcessingReport report=null;
		try {
			report = schema.validate(spec,true);
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
			if(pm.getLogLevel().equals(LogLevel.ERROR)) {
			    output.addValidationMessage(new SchemaValidationError(pm.asJson()));
			}else if(pm.getLogLevel().equals(LogLevel.WARNING)){
				log.warn(Json.pretty(pm.asJson()));
			}
		}
		return output;
	}

	 private SwaggerParseResult readOpenApi(String content) throws IllegalArgumentException {
	        OpenAPIV3Parser parser = new OpenAPIV3Parser();
	        return parser.readContents(content, null, null);

	    }
	 
	    private JsonNode readNode(String text) {
	        try {
	            if (text.trim().startsWith("{")) {
	                return JsonMapper.readTree(text);
	            } else {
	                return YamlMapper.readTree(text);
	            }
	        } catch (IOException e) {
	            return null;
	        }
	    }
}
