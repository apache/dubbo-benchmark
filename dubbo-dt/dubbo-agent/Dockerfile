FROM maven:3.8.4-jdk-8 AS builder

WORKDIR /build
COPY . /build/

WORKDIR /build
RUN mvn install -DskipTests

FROM eclipse-temurin:8-jdk
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

WORKDIR /app

COPY --from=builder /build/dubbo-agent/target/dubbo-agent-*.jar app.jar

COPY --from=builder /build/dubbo-agent/target/lib /app/lib/

EXPOSE 8082
CMD ["java", "-jar", "app.jar"]