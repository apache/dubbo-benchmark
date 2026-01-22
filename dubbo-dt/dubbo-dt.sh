#!/bin/bash
set -e

clear
echo "============================================="
echo "   Dubbo Load Balancer Testing Tool          "
echo "============================================="

echo ""
echo "Service Configuration / 服务配置"

read -p "Enter number of Dubbo Providers (default: 10) / 输入Provider数量(默认: 10): " PROVIDER_NUM
PROVIDER_NUM=${PROVIDER_NUM:-10}

read -p "Enter number of Dubbo Consumers (default: 1) / 输入Consumer数量(默认: 1): " CONSUMER_NUM
CONSUMER_NUM=${CONSUMER_NUM:-1}

echo ""
echo "Configuration Info / 配置信息"
echo "Provider Count: $PROVIDER_NUM"
echo "Consumer Count: $CONSUMER_NUM"

echo ""
echo "Configure Agent Test Parameters / 配置Agent测试参数"
read -p "Agent Namespace / agent的命名空间 (默认/Default:dubbo-agent):" DUBBO_AGENT
DUBBO_AGENT=${DUBBO_AGENT:-dubbo-agent}
read -p "Enter Agent load balancing strategy / 输入Agent负载均衡策略(默认/Default:ConsistentHash): " AGENT_LB
AGENT_LB=${AGENT_LB:-ConsistentHash}
read -p "Enter Agent total test mode / 输入Agent的测试方法(FIXED_COUNT : 固定次数模式/DURATION: 持续时长模式 默认/Default: FIXED_COUNT): " AGENT_TEST_MODE
AGENT_TEST_MODE=${AGENT_TEST_MODE:-FIXED_COUNT}
read -p "Enter Agent test duration in seconds / 输入Agent测试持续时间(秒) (默认/Default:100): " AGENT_DURATION
AGENT_DURATION=${AGENT_DURATION:-100}

read -p "Enter Agent total test requests / 输入Agent测试请求总数 (默认/Default:100) : " AGENT_REQUEST_CNT
AGENT_REQUEST_CNT=${AGENT_REQUEST_CNT:-100}

read -p "Enter Agent serialization method / 输入Agent序列化方式 (默认/Defaul:hessian2): " AGENT_SERIALIZE
AGENT_SERIALIZE=${AGENT_SERIALIZE:-hessian2}

PROVIDER_SERVICE_START_PORT=8080
PROVIDER_DUBBO_START_PORT=20880
CONSUMER_START_PORT=8180

COMPOSE_FILE="docker-compose.yml"
echo ""
echo "Generating docker-compose file / 生成docker-compose文件..."

# Start generating docker-compose.yml
cat > $COMPOSE_FILE << EOF
version: '3.8'

services:
  nacos:
    image: nacos/nacos-server:v2.5.2
    container_name: nacos-server
    environment:
      - PREFER_HOST_MODE=hostname
      - MODE=standalone
      - SPRING_DATASOURCE_PLATFORM=
      - NACOS_AUTH_ENABLE=false
      - NACOS_JVM_XMS=512m
      - NACOS_JVM_XMX=512m
      - NACOS_JVM_XMN=256m
    ports:
      - "8848:8848"
    volumes:
      - nacos_data:/home/nacos/data
      - nacos_logs:/home/nacos/logs
    networks:
      - dubbo-network
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8848/nacos/v1/console/health/readiness"]
      interval: 20s
      timeout: 10s
      retries: 20
      start_period: 90s

  dubbo-agent:
    build:
      context: .
      dockerfile: ./dubbo-agent/Dockerfile
    container_name: ${DUBBO_AGENT}
    environment:
      - SERVER_PORT=8082
      - SPRING_APPLICATION_NAME=dubbo-agent
      - NACOS_HOST=nacos-server
      - AGENT_LOADBALANCE=${AGENT_LB}
      - AGENT_DURATION_SECONDS=${AGENT_DURATION}
      - AGENT_REQUEST_COUNT=${AGENT_REQUEST_CNT}
      - AGENT_SERIALIZATION=${AGENT_SERIALIZE}
      - TEST_DEFAULT_TIMEOUT_SECONDS=300
      - AGENT_TEST_MODE=${AGENT_TEST_MODE}
      - TEST_MAX_CONCURRENT_TESTS=10
      - JAVA_OPTS=-Xmx512m -Xms256m -XX:+UseG1GC -Dfile.encoding=UTF-8
      - DUBBO_REGISTRY_RETRY_TIMES=15
      - DUBBO_REGISTRY_RECONNECT_PERIOD=5000
      - DUBBO_SHUTDOWN_WAIT_SECONDS=60
    ports:
      - "8082:8082"
    volumes:
      - ./logs/agent:/app/logs
      - .:/app
    depends_on:
      nacos:
        condition: service_healthy
    networks:
      - dubbo-network
    restart: unless-stopped
EOF

# Generate Providers
for (( i=1; i<=PROVIDER_NUM; i++ ))
do
  PROVIDER_SERVICE_PORT=$((PROVIDER_SERVICE_START_PORT + i - 1))
  PROVIDER_DUBBO_PORT=$((PROVIDER_DUBBO_START_PORT + i - 1))

  if [ $i -eq 1 ]; then
    CONTAINER_NAME="dubbo-provider"
    LOG_DIR="provider"
  else
    CONTAINER_NAME="dubbo-provider-$i"
    LOG_DIR="provider-$i"
  fi

  cat >> $COMPOSE_FILE << EOF

  ${CONTAINER_NAME}:
    build:
      context: .
      dockerfile: ./dubbo-provider/Dockerfile
    container_name: ${CONTAINER_NAME}
    environment:
      - SERVICE_PORT=${PROVIDER_SERVICE_PORT}
      - SPRING_APPLICATION_NAME=${CONTAINER_NAME}
      - NACOS_HOST=nacos-server
      - AGENT_HOST=dubbo-agent
      - DUBBO_REGISTER_MODE=instance
      - AGENT_PORT=8082
      - DUBBO_PROTOCOL_PORT=${PROVIDER_DUBBO_PORT}
      - DUBBO_REGISTER_MODE=instance
      - DUBBO_PROTOCOL_NAME=dubbo
      - DUBBO_LOADBALANCE=leastactive
      - JAVA_OPTS=-Xmx512m -Xms256m -XX:+UseG1GC -Dfile.encoding=UTF-8
      - DUBBO_REGISTRY_RETRY_TIMES=20
      - DUBBO_REGISTRY_RECONNECT_PERIOD=8000
      - DUBBO_SHUTDOWN_WAIT_SECONDS=60
      - DUBBO_REGISTRY_CHECK=false
    ports:
      - "${PROVIDER_SERVICE_PORT}:${PROVIDER_SERVICE_PORT}"
      - "${PROVIDER_DUBBO_PORT}:${PROVIDER_DUBBO_PORT}"
    volumes:
      - ./logs/${LOG_DIR}:/app/logs
      - dubbo-cache:/root/.dubbo
    depends_on:
      nacos:
        condition: service_healthy
    networks:
      - dubbo-network
    restart: unless-stopped
EOF
done

# Generate Consumers
for (( i=1; i<=CONSUMER_NUM; i++ ))
do
  CONSUMER_PORT=$((CONSUMER_START_PORT + i - 1))

  if [ $i -eq 1 ]; then
    CONTAINER_NAME="dubbo-consumer"
    LOG_DIR="consumer"
  else
    CONTAINER_NAME="dubbo-consumer-$i"
    LOG_DIR="consumer-$i"
  fi

  cat >> $COMPOSE_FILE << EOF

  ${CONTAINER_NAME}:
    build:
      context: .
      dockerfile: ./dubbo-consumer/Dockerfile
    container_name: ${CONTAINER_NAME}
    environment:
      - CONSUMER_PORT=${CONSUMER_PORT}
      - SPRING_APPLICATION_NAME=${CONTAINER_NAME}
      - AGENT_HOST=dubbo-agent
      - AGENT_PORT=8082
      - NACOS_HOST=nacos-server
      - DUBBO_CONSUMER_LOADBALANCE=roundrobin
      - DUBBO_QOS_ENABLE=false
      - JAVA_OPTS=-Xmx512m -Xms256m -XX:+UseG1GC -Dfile.encoding=UTF-8
      - DUBBO_REGISTRY_RETRY_TIMES=25
      - DUBBO_REGISTRY_RECONNECT_PERIOD=10000
      - DUBBO_SHUTDOWN_WAIT_SECONDS=60
      - DUBBO_REGISTRY_CHECK=false
      - DUBBO_WAIT_FOR_SERVICES_TIMEOUT=180000
    ports:
      - "${CONSUMER_PORT}:${CONSUMER_PORT}"
    volumes:
      - ./logs/${LOG_DIR}:/app/logs
      - dubbo-cache:/root/.dubbo
    depends_on:
      nacos:
        condition: service_healthy
      dubbo-agent:
        condition: service_started
EOF

  # Add dependencies for all providers
  for (( p=1; p<=PROVIDER_NUM; p++ ))
  do
    if [ $p -eq 1 ]; then
      DEP_NAME="dubbo-provider"
    else
      DEP_NAME="dubbo-provider-$p"
    fi
    echo "      ${DEP_NAME}:" >> $COMPOSE_FILE
    echo "        condition: service_started" >> $COMPOSE_FILE
  done

  cat >> $COMPOSE_FILE << EOF
    networks:
      - dubbo-network
    restart: unless-stopped
EOF
done

# Add Dubbo Admin
cat >> $COMPOSE_FILE << EOF

  dubbo-admin:
    image: apache/dubbo-admin:0.5.0
    container_name: dubbo-admin
    environment:
      - admin.registry.address=nacos://nacos-server:8848
      - admin.config-center=nacos://nacos-server:8848
      - admin.metadata-report.address=nacos://nacos-server:8848
    ports:
      - "8083:8080"
    depends_on:
      nacos:
        condition: service_healthy
    networks:
      - dubbo-network
    restart: unless-stopped

networks:
  dubbo-network:
    driver: bridge

volumes:
  nacos_data:
  nacos_logs:
  dubbo-cache:
EOF

echo ""
echo "✅ Generation completed! / 生成完成！"
echo "File location: $(pwd)/$COMPOSE_FILE / 文件位置: $(pwd)/$COMPOSE_FILE"
echo ""
echo "Service Port Mapping / 服务端口映射:"
echo "--------------------------------------"
echo "Nacos: localhost:8848"
echo "Dubbo Admin: localhost:8083"
echo "Dubbo Agent: localhost:8082"
echo ""
for (( i=1; i<=PROVIDER_NUM; i++ ))
do
  PROVIDER_SERVICE_PORT=$((PROVIDER_SERVICE_START_PORT + i - 1))
  PROVIDER_DUBBO_PORT=$((PROVIDER_DUBBO_START_PORT + i - 1))
  if [ $i -eq 1 ]; then
    echo "Dubbo Provider 1: localhost:${PROVIDER_SERVICE_PORT} (dubbo://localhost:${PROVIDER_DUBBO_PORT})"
  else
    echo "Dubbo Provider ${i}: localhost:${PROVIDER_SERVICE_PORT} (dubbo://localhost:${PROVIDER_DUBBO_PORT})"
  fi
done

for (( i=1; i<=CONSUMER_NUM; i++ ))
do
  CONSUMER_PORT=$((CONSUMER_START_PORT + i - 1))
  if [ $i -eq 1 ]; then
    echo "Dubbo Consumer 1: localhost:${CONSUMER_PORT}"
  else
    echo "Dubbo Consumer ${i}: localhost:${CONSUMER_PORT}"
  fi
done
echo "--------------------------------------"
echo ""
echo "Start command: / 启动命令:"
echo "docker-compose -f $COMPOSE_FILE up -d --build"
echo ""
echo "Stop command: / 停止命令:"
echo "docker-compose -f $COMPOSE_FILE down -v"
echo "============================================="