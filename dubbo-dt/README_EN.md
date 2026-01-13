

# Dubbo Testing Tool

https://img.shields.io/badge/build-passing-brightgreen

This project focuses on testing the performance of the Dubbo framework. Using Docker containerization technology, it helps developers quickly test the actual usage of load balancing algorithms in their projects.

## How to Run Tests

#### Method 1: Docker Deployment:

##### First execute the command:

```
chmod +x dubbo-dt.sh
```

##### **Execute the command**

```
./dubbo-dt.sh
```

##### Finally execute:

```
docker compose up -d
```

#### (Optional) Configuration Parameters:

##### **You can press Enter to accept all defaults, or modify parameters as needed**

| Input Parameters                                             | Default        |
| :----------------------------------------------------------- | :------------- |
| Enter number of Dubbo Consumers                              | 1              |
| Enter Agent load balancing strategy                          | ConsistentHash |
| Enter Agent total test mode (FIXED_COUNT: Fixed Count Mode / DURATION: Duration Mode) | FIXED_COUNT    |
| Enter Agent test duration in seconds                         | 100 seconds    |
| Enter Agent total test requests                              | 100 requests   |
| Enter Agent serialization method                             | hessian2       |
| Agent Namespace (used for connecting "consumer" and "provider") | dubbo-agent    |
| Enter number of Dubbo Producers                              | 10             |

#### (Optional) Output Files: Will be generated in the current directory

| File Name                                     | Description                                                  |
| :-------------------------------------------- | :----------------------------------------------------------- |
| fixed_count-consumer-dubbo-consumerCN/EN.html | Consumer results. Naming convention: test_method-tester(consumer or provider)-name_CN/EN.html |
| consumer_resultsayHello.txt                   | Test results in JSON format. Naming convention: tester-result-method_name |
| provide_resultprovider_1CN/EN.html            | Provider data. Naming convention: provider-result-tester_name |
| provide_resultprovider_1.txt                  | Test results in JSON format. Naming convention: provider-result-tester_name |



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