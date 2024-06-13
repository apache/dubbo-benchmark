# 使用Java官方镜像作为基础镜像
FROM openjdk:8-jdk-alpine

ENV JAVA_OPTS="-Xmx512m -Xms512m"
ENV TZ="Asia/Shanghai"
ENV NACOS_ADDRESS="121.250.209.74"

# 设置容器内工作目录
RUN mkdir /app

# 暴露Dubbo应用使用的端口
EXPOSE 20880 50052 22222

# 将构建好的jar文件复制到容器内
COPY target/dubbo-dubbo-server.jar /app/dubbo-dubbo-server.jar

# 设置容器启动时执行的命令
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app/dubbo-dubbo-server.jar"]