# OpenAPI文档工具
这是一个分析Spring项目中的Java源代码(注释)以及class文件生成openapi文档的工具.
## 功能特性
- 提供集中式的接口文档管理工具(集成Swagger-UI，Swagger-Editor)。
- 分析注释生成openapi接口文档，无需Swagger注解。
- 支持分析外部源代码Jar文件。
- 提供Maven插件用于项目集成。
- 支持生成Markdown文档。
## 使用
### 部署接口文档管理工具
1. 编译成docker镜像
   ```shell
   cd swagger-ui
   mvn compile jib:buildTar
   ```
2. 导入docker镜像
   ```shell
   docker image load -i target/jib-image.tar 
   ```
3. 配置运行
   ```shell
   docker run --rm -ti -e SYSTEM_NAME="XXX系统接口文档" -p 8080:80/tcp 192.168.0.140:8082/hnvmns/swagger-ui:0.0.2
   ```
   docker-compose参考
   ```yml
   version: "2.4"
   services:
     hnvmns9000:
      image: hnvmns/swagger-ui:0.0.2
      restart: always
      ports:
        - 9000:80/tcp
      volumes:
        - /data/app/apidoc/hnvmns9000/data:/data
        - /data/app/apidoc/hnvmns9000/history:/history
      mem_limit: "1024M"
      environment:
        SYSTEM_NAME: "XXX系统接口文档"
   ```
4. 打开浏览器访问主页：http://localhost:8080/doc/
### 项目集成Maven插件
1. 加入插件坐标
   ```xml
   			<plugin>
				<groupId>com.hngd.tool</groupId>
				<artifactId>codegen-maven-plugin</artifactId>
				<version>2.4.0-SNAPSHOT</version>
				<configuration>
					<!-- 模块根请求路径,如果是请求是从网关转发到此模块,则需要配置为'/'+模块名称,本机测试配置为'/' -->
					<serviceUrl>/sample</serviceUrl>
					<!-- 配置生成js 客户端代码的类型,目前支持ajax与axios -->
					<type>axios</type>
					<!-- <outputDirectory>${project.basedir}/src/main/resources/static/js</outputDirectory> -->
					<!-- 配置接口类所在包名称 -->
					<packageFilter>com.hngd.web.controller</packageFilter>
					<!-- 配置生成Java客户端代码的包名 -->
					<apiPackage>com.hngd.web.api</apiPackage>
					<!-- 配置生成java客户端代码的同步or异步调用,支持async,sync -->
					<invokeType>async</invokeType>
					<!-- 配置生成Swagger文档的基础信息配置文件所在位置 <confFilePath>${project.basedir}/build-config/swagger-config.json</confFilePath> -->
					<!--Swagger UI 服务地址,配置后接口文档将自动上传到该服务 -->
					<swaggerUIServer>localhost:8080</swaggerUIServer>
					<excludes>**/com/hngd/model/*Example.java,**/com/hngd/dao/*.java</excludes>
					<includes>**/com/hngd/**/*.java,com/hngd/**/*.java</includes>
					<openAPIServerURL>https://192.168.0.140:8899/api</openAPIServerURL>
				</configuration>
			</plugin>
   ```
2. 生成接口文档并上传到接口文档管理工具
   ```shell
   mvn codegen:openapi
   ```