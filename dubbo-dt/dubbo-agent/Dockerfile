# 多阶段构建 - 修复依赖问题
FROM maven:3.8.4-jdk-8 AS builder

WORKDIR /build
COPY . /build/

# 构建项目
RUN echo "=== 构建项目 ===" && \
    echo "1. 完整构建..." && \
    mvn clean package -DskipTests && \
    echo "2. 验证构建结果..." && \
    if [ -d "dubbo-agent/target" ]; then \
        echo "JAR文件:" && \
        find dubbo-agent/target -name "*.jar" -type f | xargs ls -lh && \
        echo "依赖库:" && \
        ls -lh dubbo-agent/target/lib/ 2>/dev/null || echo "无依赖库目录"; \
    fi

FROM eclipse-temurin:8-jdk
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

WORKDIR /app

COPY --from=builder /build/dubbo-agent/target/dubbo-agent-*.jar app.jar

COPY --from=builder /build/dubbo-agent/target/lib /app/lib/

RUN echo "=== 验证文件结构 ===" && \
    echo "应用目录:" && \
    ls -la /app/ && \
    echo -e "\n依赖库目录:" && \
    ls -la /app/lib/ 2>/dev/null || echo "无lib目录" && \
    echo -e "\n验证JAR的Class-Path:" && \
    jar xf app.jar META-INF/MANIFEST.MF && \
    echo "Main-Class: $(grep 'Main-Class' META-INF/MANIFEST.MF)" && \
    echo "Class-Path: $(grep 'Class-Path' META-INF/MANIFEST.MF | head -1)"

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]