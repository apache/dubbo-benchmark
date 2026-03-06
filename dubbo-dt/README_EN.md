# Dubbo Testing Tool

https://img.shields.io/badge/build-passing-brightgreen

This project focuses on testing the performance of the Dubbo framework. Through Docker containerization technology, it helps developers quickly test the actual usage of load balancing algorithms in their projects.

## How to Run Tests（When the Agent namespace is agentProto, it is the proto test）

### Method 1: Docker Deployment

#### 1. Execution Commands

```bash
# Enter the project directory
cd dubbo-dt

# Add execution permissions
chmod +x dubbo-dt.sh

# Run the configuration script (press Enter to use all defaults)
./dubbo-dt.sh

# Start all services
docker compose up -d

#go to the results directory
cd result
```



#### 2. Configuration Parameters (Optional)

| Parameter                     | Description                         | Default Value                                                |
| :---------------------------- | :---------------------------------- | :----------------------------------------------------------- |
| Dubbo Consumer Count          | Enter the number of Consumers       | 1                                                            |
| Agent Load Balancing Strategy | Enter Agent load balancing strategy | ConsistentHash                                               |
| Agent Test Mode               | Enter Agent test method             | FIXED_COUNT (Default Fixed Mode) For example: SELFFUNCTION (Self-test Mode) DURATION (Duration Mode) |
| Agent Test Duration           | Enter Agent test duration (seconds) | 100 seconds                                                  |
| Agent Test Request Count      | Enter Agent total test requests     | 100 times                                                    |
| Agent Serialization Method    | Enter Agent serialization method    | hessian2                                                     |
| Agent Namespace               | Agent namespace                     | **dubbo-agent (testing without proto), agentProto (testing with proto)** |
| Dubbo Provider Count          | Enter Provider count                | 10                                                           |
| Agent Protocol                | Enter protocol name                 | dubbo                                                        |

#### 3. Generated Results (Optional)

| File Name                                     | Description              |
| :-------------------------------------------- | :----------------------- |
| fixed_count-consumer-dubbo-consumerCN/EN.html | Consumer results         |
| consumer_result_sayHello.txt                  | JSON format test results |
| provider_result_provider_1CN/EN.html          | Provider data            |
| provider_result_provider_1.txt                | JSON format test results |

### Method 2: Local Deployment

#### 1. Configure Agent Parameters

| Parameter               | Meaning               | Required | Default Value |
| :---------------------- | :-------------------- | :------- | :------------ |
| SPRING_APPLICATION_NAME | Agent name            | Yes      | None          |
| SERVICE_PORT            | Agent port            | Yes      | None          |
| AGENT_DURATION_SECONDS  | Test execution time   | No       | 100           |
| AGENT_REQUEST_COUNT     | Test count            | No       | 100           |
| AGENT_LOADBALANCE       | Load balancing method | No       | random        |
| AGENT_SERIALIZATION     | Serialization method  | No       | hessian2      |
| AGENT_TEST_MODE         | Test method           | No       | FIXED_COUNT   |
| AGENT_PROTOCOL          | Agent protocol        | No       | dubbo         |

Startup example:

bash

```bash
java -DSPRING_APPLICATION_NAME=dubbo-agent -DSERVICE_PORT=8802 -jar
```



#### 2. Configure Test Methods

**Consumer End Configuration**

Add annotation on Consumer:

```java
@EnableDubboTest(basePackages = {"com.dubbo.consumer", "com.dubbo.common"}, testModel = "consumer")
```

### Configuration Parameters:

| Parameter    | Description                                                 | Example Value         |
| :----------- | :---------------------------------------------------------- | :-------------------- |
| basePackages | Base package paths for scanning abstract service interfaces | com.dubbo.common      |
| testModel    | Service role for testing (consumer, provider, or both)      | consumer/provider/all |



Add annotation on abstract API:

```java
@DubboInvokeStat(namespace = "agentname", argKey = "AGENT_NAME_HELLO", argValue = DubboInvokeEnum.class)
```

### @DubboInvokeStat Parameters:

| Attribute | Description                       | Sample                | Required |
| :-------- | :-------------------------------- | :-------------------- | :------- |
| namespace | Agent name identifier             | "agentname"           | Yes      |
| argKey    | Key for test data lookup          | "AGENT_NAME_HELLO"    | Yes      |
| argValue  | Class containing test data values | DubboInvokeEnum.class | Yes      |

**Mock Data Enumeration Class**

```java
public enum DubboInvokeEnum {
    AGENT_NAME_HELLO("Hello"),
    AGENT_NAME_HELLO2("Hello2"),
    AGENT_OTHER("Extended test"),
    AGENT_PROTO(UserProto.UserRequest.newBuilder()
            .setName("HELLO")
            .build());
    
    private Object value;
    
    DubboInvokeEnum(Object reqObj) {
        this.value = reqObj;
    }
    
    public Object getValue() {
        return value;
    }
}
```



**Provider End Configuration**

Add annotation on Provider:

```java
@EnableDubboTest(basePackages = {"com.dubbo.consumer", "com.dubbo.common"}, testModel = "provider")
```

### Configuration Parameters:

| Parameter    | Description                                                 | Example Value         |
| :----------- | :---------------------------------------------------------- | :-------------------- |
| basePackages | Base package paths for scanning abstract service interfaces | com.dubbo.common      |
| testModel    | Service role for testing (consumer, provider, or both)      | consumer/provider/all |

### Protocol Adaptation:

> ***The framework supports multiple protocols with different initialization methods. Any abstract interface annotated with @DubboInvokeStat within packages scanned by @EnableDubboTest is automatically available for testing.***<!-- Remember to compile the source files with Maven before starting -->

## Result:Dubbo Performance Test Result File Naming Convention

### File Naming Rules Overview

| File Category                      | File Prefix            | Middle Part     | Suffix | Language Code | File Format | Description                                                  |
| :--------------------------------- | :--------------------- | :-------------- | :----- | :------------ | :---------- | :----------------------------------------------------------- |
| **Consumer Test Detailed Results** | `consumer-result-`     | `{methodName}`  | -      | -             | `.txt`      | Records detailed invocation information for specific methods, including response time, status, and other raw data |
| **Provider Test Detailed Results** | `provide-result-`      | `{serviceName}` | -      | -             | `.txt`      | Records raw test data for specific provider services, including invocation statistics and performance metrics |
| **Consumer Test Report**           | `{testMode}-consumer-` | `{serviceName}` | -      | `CN`/`EN`     | `.html`     | Visual test report for consumer services, containing performance charts and statistical summaries |
| **Provider Trend Analysis Report** | `provide-ressult-`     | `{serviceName}` | -      | `CN`/`EN`     | `.html`     | Call trend analysis report for provider services, displaying time-series performance data |