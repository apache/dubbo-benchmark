# 构建阶段
FROM maven:3.8.4-openjdk-11 AS builder
WORKDIR /app

# 复制依赖文件
COPY pom.xml .
COPY ../dubbo-common/pom.xml ../dubbo_common/
RUN mvn dependency:go-offline -N

# 复制源代码
COPY src ./src
COPY ../dubbo-common/src ../dubbo_common/src

# 构建应用
RUN mvn clean package -DskipTests

# 运行阶段
FROM openjdk:11-jre-slim
WORKDIR /app

# 安装curl用于健康检查
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# 复制构建结果
COPY --from=builder /app/target/*.jar app.jar

# 环境变量
ENV PROVIDER_ID=1
ENV NACOS_HOST=nacos
ENV AGENT_HOST=agent
ENV AGENT_PORT=8888

# 暴露端口
EXPOSE 20880 8080 22222

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]