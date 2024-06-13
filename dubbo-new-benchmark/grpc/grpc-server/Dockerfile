# 使用Java官方镜像作为基础镜像
FROM openjdk:8-jdk-alpine

ENV JAVA_OPTS="-Xmx512m -Xms512m"
ENV TZ="Asia/Shanghai"
ENV NACOS_ADDRESS="nacos.dubbo-benchmark.svc.cluster.local"
ENV DB_HOST="postgres-service.dubbo-benchmark.svc.cluster.local"
ENV PROM_URL="kube-prometheus-stack-prometheus.monitoring.svc.cluster.local"

# 设置容器内工作目录
RUN mkdir /app

# 暴露Grpc应用使用的端口
EXPOSE 50053 50055

# 将构建好的jar文件复制到容器内
COPY target/grpc-server.jar /app/grpc-server.jar

# 设置容器启动时执行的命令
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app/grpc-server.jar"]