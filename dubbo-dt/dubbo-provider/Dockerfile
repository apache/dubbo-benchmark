FROM maven:3.8.4-jdk-8 AS builder

ENV TZ=Asia/Shanghai
ENV MAVEN_OPTS="-Dmaven.repo.url=https://maven.aliyun.com/repository/public -Dmaven.repo.local=/root/.m2/repository"

WORKDIR /build

COPY . /build/

RUN pwd && ls -la && \
    mvn clean install -DskipTests -N && \
    if [ -d "dubbo-common" ]; then \
        mvn clean install -DskipTests -pl dubbo-common -am; \
    fi && \
    if [ -d "dubbo-provider" ]; then \
        cd dubbo-provider && \
        mvn clean package spring-boot:repackage -DskipTests && \
        find target -name "*.jar" -type f | xargs ls -lh && \
        find target -name "original-*.jar" -type f -exec echo "  - {}" \; && \
        find target -name "dubbo-provider*.jar" ! -name "original*" -type f -exec echo "  - {}" \; && \
        for jar in $(find target -name "dubbo-provider*.jar" ! -name "original*" -type f); do \
            jar tf "$jar" | grep -E "(MANIFEST.MF|Main-Class)" | head -3; \
        done; \
    fi

FROM eclipse-temurin:8-jdk

ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

WORKDIR /app
RUN mkdir -p /app/logs && mkdir -p /root/.dubbo

COPY --from=builder /build/dubbo-provider/target/dubbo-provider-*.jar app.jar

RUN ls -lh /app/ && \
    if command -v jar >/dev/null 2>&1; then \
        if jar tf app.jar | grep -q "MANIFEST.MF"; then \
            jar xf app.jar META-INF/MANIFEST.MF 2>/dev/null && \
            if grep -qi "main-class" META-INF/MANIFEST.MF; then \
                true; \
            fi; \
        fi; \
    fi

EXPOSE 8080 20880

HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health 2>/dev/null || curl -f http://localhost:8080/ 2>/dev/null || exit 1
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]