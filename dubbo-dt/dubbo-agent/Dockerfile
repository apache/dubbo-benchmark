
FROM maven:3.8.4-jdk-8 AS builder

WORKDIR /build
COPY . /build/

RUN mvn clean package -DskipTests && \
    if [ -d "dubbo-agent/target" ]; then \
        find dubbo-agent/target -name "*.jar" -type f | xargs ls -lh && \
        ls -lh dubbo-agent/target/lib/ 2>/dev/null || echo "无依赖库目录/Directory without dependency libraries"; \
    fi

FROM eclipse-temurin:8-jdk
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

WORKDIR /app

COPY --from=builder /build/dubbo-agent/target/dubbo-agent-*.jar app.jar

COPY --from=builder /build/dubbo-agent/target/lib /app/lib/

RUN ls -la /app/ && \
    ls -la /app/lib/ 2>/dev/null || echo "无lib目录/No "lib" directory" && \
    jar xf app.jar META-INF/MANIFEST.MF && \
    echo "Main-Class: $(grep 'Main-Class' META-INF/MANIFEST.MF)" && \
    echo "Class-Path: $(grep 'Class-Path' META-INF/MANIFEST.MF | head -1)"

EXPOSE 8082

ENTRYPOINT ["java", "-cp", "app.jar:lib/*", "com.dubbo.dlt.AgentApplication"]