# Dubbo Testing Tool

https://img.shields.io/badge/build-passing-brightgreen

This project focuses on testing the performance of the Dubbo framework. Using Docker containerization technology, it helps developers quickly test the actual usage of load balancing algorithms in their projects.

## How to Run Tests

#### Method 1: Docker Deployment

##### First, execute the command:

text

```
cd dubbo-dt.sh
```

**Then execute:**

```
chmod +x dubbo-dt.sh
```

##### **Execute command**

```
./dubbo-dt.sh
```

##### Finally, execute:

```
docker compose up -d
```



#### (Optional) Configuration Parameters:

##### **You can press Enter to accept all defaults, or modify parameters as needed.**

| Input Parameter / Description                          | Default        |
| :----------------------------------------------------- | :------------- |
| Enter number of Dubbo Consumers (default: 1)           | 1              |
| Enter Agent load balancing strategy                    | ConsistentHash |
| Enter Agent total test mode (FIXED_COUNT / DURATION)   | FIXED_COUNT    |
| Enter Agent test duration in seconds                   | 100 seconds    |
| Enter Agent total test requests                        | 100 requests   |
| Enter Agent serialization method                       | hessian2       |
| Agent Namespace (for connecting consumer and provider) | dubbo-agent    |
| Enter number of Dubbo Providers (default: 10)          | 10             |

#### (Optional) Result Files: Generated in the current directory

| File Name                                     | Description                                                  |
| :-------------------------------------------- | :----------------------------------------------------------- |
| fixed_count-consumer-dubbo-consumerCN/EN.html | Consumer results. Naming convention: testMethod-tester(consumer/provider)-name_CN/EN.html |
| consumer_resultsayHello.txt                   | Test results in JSON format. Naming: tester-result-methodName |
| provide_resultprovider_1CN/EN.html            | Provider data. Naming: provider-result-testerName            |
| provide_resultprovider_1.txt                  | Test results in JSON format. Naming: provider-result-testerName |

#### Method 2: Local Deployment (requires some Dubbo experience):

###### Clone this project locally

**1. First, configure the agent parameters**

| Parameter Name          | Meaning                   | Required?                  |
| :---------------------- | :------------------------ | :------------------------- |
| SPRING_APPLICATION_NAME | Agent name                | Yes                        |
| SERVICE_PORT            | Agent port                | Yes                        |
| AGENT_DURATION_SECONDS  | Agent test execution time | No (default: 100)          |
| AGENT_REQUEST_COUNT     | Number of agent requests  | No (default: 100)          |
| AGENT_LOADBALANCE       | Load balancing strategy   | Yes (default: random)      |
| AGENT_SERIALIZATION     | Serialization method      | Yes (default: hessian2)    |
| AGENT_TEST_MODE         | Testing mode              | Yes (default: FIXED_COUNT) |

Example: `java -DSPRING_APPLICATION_NAME=dubbo-agent -DSERVICE_PORT=8802 -jar`

##### 2. How to configure your own test methods

##### Consumer Configuration

**First, add the annotation to the Consumer's startup class:**

java

```java
@SpringBootApplication(scanBasePackages = {"com.dubbo.common","com.dubbo.consumer"})
@EnableDubbo
@EnableDubboTest(basePackages = {"com.dubbo.consumer", "com.dubbo.common"}, testModel = "consumer")
public class ConsumerApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = 
            SpringApplication.run(ConsumerApplication.class, args);
    }
}
```



**@EnableDubboTest**

> **basePackages**: API package path
> **testModel**: Write "consumer" if current test mode is consumer; omit if both consumer and provider

**Add annotation to the API method being tested:**

java

```
 @DubboInvokeStat(namespace = "agentname", argKey = "AGENT_NAME_HELLO", argValue = DubboInvokeEnum.class) 
```



**@DubboInvokeStat**

> **namespace**: Name of the agent for this test
> **argKey**: Name of the key corresponding to your enum
> **argValue**: Corresponding test data enum

##### Mock Data Enum Class (where your test data is written)

###### Important: key is the name of mock data to test, value contains the corresponding values

java

```java
package com.dubbo.common.constant;
import com.alibaba.fastjson2.JSON;

public enum DubboInvokeEnum  {
    AGENT_NAME_HELLO(new DubboTest("Hello")),
    AGENT_NAME_HELLO2(new DubboTest("Hello2")),
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



##### DubboTest (Parameters required for test data)

###### Requirement: These parameters must match the method name being called

```java
package com.dubbo.common.constant;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DubboTest<T> {
    private T name;
}
```



##### Provider Configuration

- ###### First, add the annotation to the Provider being tested:

```java
@EnableDubboTest(basePackages = {"com.dubbo.consumer", "com.dubbo.common"}, testModel = "provider")
```



**@EnableDubboTest**

> **basePackages**: API package path
> **testModel**: Write "provider" if current test mode is provider; omit if both consumer and provider