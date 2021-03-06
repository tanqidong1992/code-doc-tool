# OpenAPI文档工具
这是一个分析Spring项目中的Java源代码(注释)以及class文件生成OpenAPI接口文档的工具.
## 功能特性
- 提供集中式的接口文档管理工具(集成Swagger-UI，Swagger-Editor)。
- 分析注释生成OpenAPI接口文档，无需Swagger注解。
- 支持分析Maven外部源代码Jar文件。
- 提供Maven插件用于项目集成。
- 支持生成Markdown文档。
- 支持OpenAPI校验。
## 使用
### 部署接口文档管理工具
1. 编译成docker镜像
   ```shell
   cd openapi-ui
   mvn compile jib:buildTar
   ```
2. 导入docker镜像
   ```shell
   docker image load -i target/jib-image.tar 
   ```
3. 配置运行
   ```shell
   docker run --rm -ti -e SYSTEM_NAME="XXX系统接口文档" -p 8080:80/tcp hnvmns/openapi-ui:0.0.2
   ```
   docker-compose参考
   ```yml
   version: "2.4"
   services:
     test:
      image: hnvmns/openapi-ui:0.0.2
      restart: always
      ports:
        - 9000:80/tcp
      volumes:
        - /data/app/apidoc/test/data:/data
        - /data/app/apidoc/test/history:/history
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
                <version>2.6.0-SNAPSHOT</version>
                <configuration>
                    <!-- controller类所在包名称 -->
                    <packageFilter>com.hngd.web.controller</packageFilter>
                    <!-- 配置生成openapi文档的基础信息配置文件所在位置 
                    <confFilePath>${project.basedir}/build-config/openapi.json</confFilePath> -->
                    <!--OpenAPI UI 服务地址,配置后接口文档将自动上传到该服务 -->
                    <openAPIUIServer>localhost:8080</openAPIUIServer>
                    <!-- 源码分析排除路径 -->
                    <excludes>**/com/hngd/model/*Example.java,**/com/hngd/dao/*.java</excludes>
                    <!-- 源码分析包含路径 -->
                    <includes>**/com/hngd/**/*.java,com/hngd/**/*.java</includes>
                    <!-- openapi接口基础地址 -->
                    <openAPIServerURL>https://localhost:8080/api</openAPIServerURL>
                </configuration>
            </plugin>
   ```
2. 生成接口文档并上传到接口文档管理工具
   ```shell
   mvn compile codegen:openapi
   ```
3. 在目录${basedir}/target/openapi下可以看到输出的openapi json文档