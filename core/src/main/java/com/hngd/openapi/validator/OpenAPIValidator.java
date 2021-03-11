package com.hngd.openapi.validator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
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
import com.hngd.constant.Constants;

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

    public ValidationResponse validate(File file) throws IOException {
        String s = FileUtils.readFileToString(file, Constants.DEFAULT_CHARSET);
        return validate(s);
    }
 
    private JsonSchema getSchemaV3() {
        try (InputStream in = getClass().getResourceAsStream(SCHEMA_PATH + "/" + SCHEMA_V3)) {
            // TODO MAX JSON FILE SIZE
            byte[] buffer = new byte[Constants.MAX_OPENAPI_FILE_SIZE];
            int size = IOUtils.read(in, buffer);
            String schemaStr = new String(buffer, 0, size, Constants.DEFAULT_CHARSET);
            JsonSchema schemaV3 = resolveJsonSchema(schemaStr, true);
            return schemaV3;
        } catch (Exception e) {
            log.error("", e);
        }
        return null;

    }

    static ObjectMapper JsonMapper = Json.mapper();
    static ObjectMapper YamlMapper = Yaml.mapper();
 
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
    public static SchemaValidationError buildError(String msg,Throwable cause) {
        ProcessingMessage pm;
        if(cause!=null) {
            pm=new ProcessingException(msg,cause).getProcessingMessage();
        }else {
            pm = new ProcessingMessage();
            pm.setLogLevel(LogLevel.ERROR);
            pm.setMessage(msg);
        }
        return new SchemaValidationError(pm.asJson());
    }
    public ValidationResponse validate(String openAPIContent) {
        ValidationResponse output = new ValidationResponse();
        JsonNode spec;
        try {
            spec = parseNode(openAPIContent);
        } catch (JsonProcessingException e) {
            SchemaValidationError error =
                buildError("Unable to read content from openAPIContent.It may be invalid JSON or YAML", e);
            output.addValidationMessage(error);
            return output;
        }
        SwaggerParseResult result = null;
        try {
            result = readOpenApi(openAPIContent);
        } catch (Exception e) {
            log.error("Parse OpenAPI content failed!", e);
            SchemaValidationError error = buildError("Unable to parse OpenAPI: " + e.getMessage(), e);
            output.addValidationMessage(error);
            return output;
        }
        for (String message : result.getMessages()) {
            output.addMessage(message);
        }
        JsonSchema schema = getSchemaV3();
        ProcessingReport report = null;
        try {
            report = schema.validate(spec, true);
        } catch (ProcessingException e) {
            log.error("", e);
            SchemaValidationError error = buildError("Validation processing failed!: " + e.getMessage(), e);
            output.addValidationMessage(error);
            return output;
        }
        ListProcessingReport lp = new ListProcessingReport();
        try {
            lp.mergeWith(report);
        } catch (ProcessingException e) {
            log.error("", e);
        }
        java.util.Iterator<ProcessingMessage> it = lp.iterator();
        while (it.hasNext()) {
            ProcessingMessage pm = it.next();
            if (pm.getLogLevel().equals(LogLevel.ERROR)) {
                output.addValidationMessage(new SchemaValidationError(pm.asJson()));
            } else if (pm.getLogLevel().equals(LogLevel.WARNING)) {
                log.warn(Json.pretty(pm.asJson()));
            }
        }
        return output;
    }

    private SwaggerParseResult readOpenApi(String content) throws IllegalArgumentException {
        OpenAPIV3Parser parser = new OpenAPIV3Parser();
        return parser.readContents(content, null, null);
    }

    private JsonNode parseNode(String text) throws JsonMappingException, JsonProcessingException {
        if (text.trim().startsWith("{")) {
            return JsonMapper.readTree(text);
        } else {
            return YamlMapper.readTree(text);
        }
    }
}
