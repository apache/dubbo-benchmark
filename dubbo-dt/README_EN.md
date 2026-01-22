

# Dubbo Test Tool

https://img.shields.io/badge/build-passing-brightgreen

This project focuses on testing the performance of the **Apache Dubbo** framework.
 By leveraging **Docker-based containerization**, it enables developers to quickly evaluate the real-world behavior of different **load balancing algorithms** in Dubbo.

------

## How to Run the Tests

### Option 1: Run with Docker

First, grant execution permission to the script:

```
chmod +x dubbo.dt.sh
```

###### You can simply press Enter to accept the default settings. If you need to make changes, you can modify the parameters as per the requirements.

#### Configurable Parameters

| Input Parameters                                             | Default        |
| ------------------------------------------------------------ | -------------- |
| Enter number of Dubbo Consumers (default: 1)                 | 1              |
| Enter Agent load balancing strategy                          | ConsistentHash |
| Enter Agent total test mode (FIXED_COUNT: fixed number of requests / DURATION: run by duration) | FIXED_COUNT    |
| Enter Agent test duration in seconds                         | 100 seconds    |
| Enter Agent total test requests                              | 100            |
| Enter Agent serialization method                             | hessian2       |
| Agent Namespace (consumer and provider are connected by this name) | dubbo-agent    |
| Enter number of Dubbo Providers (default: 10)                | 10             |

#### Finally, start the service:

```
docker compose up -d
```

------



### Results: Files will be directly generated in the current directory, including:

|                   File Name                   |                       File Explanation                       |
| :-------------------------------------------: | :----------------------------------------------------------: |
| fixed_count-consumer-dubbo-consumerCN/EN.html | Consumer test results. Naming rule: TestMethod-TestRole(consumer/provide)-Name_CN/EN.html |
|          consumer_resultsayHello.txt          | Test results in JSON format. Naming rule: TestRole-result-MethodName |
|      provide_ressultprovider_1CN/EN.html      |   Provider data. Naming rule: provider-result-TestRoleName   |
|         provide_resultprovider_1.txt          | Test results in JSON format. Naming rule: provider-result-TestRoleName |



### Option 2: Run Locally

#### Clone this project to your local machine

### 1. Configure Agent Parameters

| Parameter Name          | Description             | Required                   |
| ----------------------- | ----------------------- | -------------------------- |
| SPRING_APPLICATION_NAME | Agent application name  | Yes                        |
| SERVICE_PORT            | Agent service port      | Yes                        |
| AGENT_DURATION_SECONDS  | Test execution duration | No (default: 100)          |
| AGENT_REQUEST_COUNT     | Total request count     | No (default: 100)          |
| AGENT_LOADBALANCE       | Load balancing strategy | Yes (default: random)      |
| AGENT_SERIALIZATION     | Serialization method    | Yes (default: hessian2)    |
| AGENT_TEST_MODE         | Test mode               | Yes (default: FIXED_COUNT) |

Example:

```
java -DSPRING_APPLICATION_NAME=dubbo-agent \
     -DSERVICE_PORT=8802 \
     -jar your-agent.jar
```

------

## 2. How to Configure Your Own Test Logic

### Consumer Configuration

#### Step 1: Add annotation to the Consumer application

```
@EnableDubboTest(
    basePackages = {"com.dubbo.consumer", "com.dubbo.common"},
    testModel = "consumer"
)
```

- **basePackages**: package paths of APIs
- **testModel**: set to `consumer` if this application is a consumer
   (omit if the application is both consumer and provider)

------

#### Step 2: Add annotation to the abstract API

```
@DubboInvokeStat(
    namespace = "agentname",
    argKey = "AGENT_NAME_HELLO",
    argValue = DubboInvokeEnum.class
)
```

- **namespace**: agent name used for this test
- **argKey**: key name defined in the enum
- **argValue**: enum class containing mock request data

------

### Mock Data Enum (Test Data Definition)

> **Important:**
>  The enum **key** represents the mock data identifier,
>  and the **value** represents the actual request object.

```
package com.dubbo.common.constant;

import com.alibaba.fastjson2.JSON;

public enum DubboInvokeEnum {

    AGENT_NAME_HELLO(new DubboTest("Hello")),
    AGENT_NAME_HELLO2(new DubboTest("Hello 2")),
    AGENT_OTHER(new DubboTest("Extended Test"));

    private Object value;

    DubboInvokeEnum(Object reqObj) {
        this.value = reqObj;
    }

    public Object getValue() {
        return value;
    }
}
```

------

### DubboTest (Request Parameter Definition)

> **Requirement:**
>  The field name must match the name of the method parameter being invoked.

```
package com.dubbo.common.constant;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DubboTest<T> {
    private T name;
}
```

------

### Provider Configuration

#### Step 1: Add annotation to the Provider application

```
@EnableDubboTest(
    basePackages = {"com.dubbo.consumer", "com.dubbo.common"},
    testModel = "provider"
)
```

- **basePackages**: package paths of APIs
- **testModel**: set to `provider` if this application is a provider
   (omit if the application is both consumer and provider)