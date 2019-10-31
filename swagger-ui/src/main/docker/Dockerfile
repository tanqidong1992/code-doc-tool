FROM anapsix/alpine-java:8u192b12_server-jre
ADD config config
ADD data data
ADD hnvmns-swagger-ui-0.0.1-SNAPSHOT.jar app.jar
ENV JAVA_OPTS="-Dspring.profiles.active=docker"
ENV LANG="zh_CN.UTF-8"
ENV TZ="Asia/Shanghai"
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar" ]