package com.hngd.openapi;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.reflect.TypeToken;
import com.hngd.parser.source.CommentStore;
import com.hngd.parser.source.SourceParserContext;

import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import lombok.Data;

public class TypeResolverTest {
	SourceParserContext pc=new SourceParserContext(null,null);
	@Before
	public void setup() throws IOException {
		pc.initSource(new File("."));
	}
	
	@Data
	public static class EntityWithJson{
		
		/**
		 * 名称
		 */
		@JsonProperty("alias")
		private String name;
		/**
		 * 年龄
		 */
		private Integer age;
	}
	
	@Test
	public void testJsonProperty() {
		TypeResolver resolver=new TypeResolver(pc.getCommentStore());
		Type type=new TypeToken<EntityWithJson>() {}.getType();
		OpenAPI swagger=new OpenAPI();
		String schemaKey=resolver.resolveAsSchema(type, swagger);
		Schema<?> schema=swagger.getComponents().getSchemas().get(schemaKey);
		Map<String, Schema> properties=schema.getProperties();
				Assert.assertTrue(properties.size()==2);
		Assert.assertTrue(properties.containsKey("age") &&
				properties.containsKey("alias"));
		Assert.assertTrue(properties.get("alias").getDescription().equals("名称"));
		Json.prettyPrint(swagger);
	}
	
	@Test
	public void testSimpleType() {

		CommentStore commentStore=new CommentStore();
		TypeResolver resolver=new TypeResolver(commentStore);
		Type type=new TypeToken<List<String>>() {}.getType();
		OpenAPI swagger=new OpenAPI();
		String schemaKey=resolver.resolveAsSchema(type, swagger);
		Assert.assertTrue(schemaKey==null);
		Json.prettyPrint(swagger);
	}

	@Test
	public void testParameterizedType() {
		CommentStore commentStore=pc.getCommentStore();
		TypeResolver resolver=new TypeResolver(commentStore);
		Type type=new TypeToken<Rest<Student>>() {}.getType();
		OpenAPI swagger=new OpenAPI();
		String schemaKey=resolver.resolveAsSchema(type, swagger);
		System.out.println("schemaKey："+schemaKey);
		Assert.assertTrue(schemaKey.equals("RestStudent"));
		Map<String,Schema> schemaMap=swagger.getComponents().getSchemas();
		schemaMap.values().forEach(this::assertSchemaNotEmptyComment);
		Json.prettyPrint(swagger);
	}

	@Test
	public void testListType() {
		CommentStore commentStore=pc.getCommentStore();
		TypeResolver resolver=new TypeResolver(commentStore);
		Type type=new TypeToken<List<Student>>() {}.getType();
		OpenAPI swagger=new OpenAPI();
		String schemaKey=resolver.resolveAsSchema(type, swagger);
		System.out.println("schemaKey："+schemaKey);
		Assert.assertTrue(schemaKey==null);
		Map<String,Schema> schemaMap=swagger.getComponents().getSchemas();
		schemaMap.values().forEach(this::assertSchemaNotEmptyComment);
		Json.prettyPrint(swagger);
	}
    private void assertSchemaNotEmptyComment(Schema schema){
		Map<String,Schema> schemaMap1=schema.getProperties();
		schemaMap1.forEach((k,s)->{
			Assert.assertTrue(StringUtils.isNotBlank(s.getDescription()));
		});

	}
	public static void main(String[]args) {
		CommentStore commentStore=new CommentStore();
		TypeResolver resolver=new TypeResolver(commentStore);
		Type type=Student.class;
		OpenAPI swagger=new OpenAPI();
		//resolver.resolveType(type, swagger);
		//resolver.resolveType(EntityB.class, swagger);
		
		Json.prettyPrint(swagger);
		type=new TypeToken<List<Student>>() {}.getType();
		Map<String, Schema> schemas = ModelConverters.getInstance().read(type);
		resolver.resolveAsSchema(type, swagger);
		schemas.forEach((k,v)->{
			swagger.schema(k, v);
		});
		
	}

	@Test
	public void testListNestedType() {
		CommentStore commentStore=pc.getCommentStore();
		TypeResolver resolver=new TypeResolver(commentStore);
		Type type=new TypeToken<StudentGroup>() {}.getType();
		OpenAPI swagger=new OpenAPI();
		String schemaKey=resolver.resolveAsSchema(type, swagger);
		System.out.println("schemaKey："+schemaKey);
		//Assert.assertTrue(schemaKey==null);
		Map<String,Schema> schemaMap=swagger.getComponents().getSchemas();
		schemaMap.values().forEach(this::assertSchemaNotEmptyComment);
		Json.prettyPrint(swagger);
	}

	@Data
	public static class Rest<T>{
		/**
		 * 错误码
		 */
		private Integer errorCode;
		/**
		 * 数据
		 */
		private T data;
	}

	@Data
	public static class Person{
		/**
		 * 人员姓名
		 */
		private String name;
		/**
		 * 人员年龄
		 */
		private Integer age;
		/**
		 * 出生日期
		 */
		private LocalDate birthday;
	}
	@Data
	public static class School{
		/**
		 * 学校名称
		 */
		private String name;
		/**
		 * 学校地址
		 */
		private Integer address;
	}
	@Data
	public static class Student extends Person{
		/**
		 * 班级名称
		 */
		private String className;
		/**
		 * 学校信息
		 */
		private School school;
	}

	@Data
	public static class StudentGroup{

		/**
		 * 团体名称
		 */
		private String name;
		/**
		 * 学生列表
		 */
		private List<Student> students;
	}
}
