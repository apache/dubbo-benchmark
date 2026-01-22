
FROM maven:3.8.4-jdk-8 AS builder

WORKDIR /build

COPY . /build/

ENV MAVEN_OPTS="-Dmaven.repo.remote=https://maven.aliyun.com/repository/public -Dmaven.repo.local=/root/.m2/repository"

RUN mvn clean install -N -DskipTests && \
    cd dubbo-common && \
    mvn clean install -DskipTests && \
    cd .. && \
    cd dubbo-consumer && \
    mvn clean package spring-boot:repackage -DskipTests && \
    ls -lh target/*.jar && \
    jar tf target/*.jar | grep -E "(DubboInvokeStat|DubboTest)" | head -10

FROM eclipse-temurin:8-jre

ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

WORKDIR /app
RUN mkdir -p /app/logs

COPY --from=builder /build/dubbo-consumer/target/dubbo-consumer-*.jar app.jar

RUN jar tf app.jar | grep -E "(DubboInvokeStat|DubboTest)" | head -10 || echo "未找到相关类/No relevant class was found"

EXPOSE 8081
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]