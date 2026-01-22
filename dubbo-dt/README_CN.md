

# Dubbo 测试工具

https://img.shields.io/badge/build-passing-brightgreen

本项目专注于测试 Dubbo 框架的性能表现。通过 Docker 容器化技术，帮助开发者快速测试项目中负载均衡算法的实际使用情况。

## 如何运行测试

#### 方式一:docker部署:

**先执行命令:** `cd dubbo-dt`

**然后执行**:`chmod +x dubbo.dt.sh`

**最后执行:** `docker compose up -d`



###### **可以直接回车全部默认，如果需要可以按照要求改参数**

##### 可以配置的参数：

| 输入的参数/input parameters                                  | 默认           |
| ------------------------------------------------------------ | :------------- |
| Enter number of Dubbo Consumers (default: 1) / 输入Consumer数量 | 1              |
| Enter Agent load balancing strategy / 输入Agent负载均衡策略  | ConsistentHash |
| Enter Agent total test mode / 输入Agent的测试方法(FIXED_COUNT : 固定次数模式/DURATION: 持续时长模式 | FIXED_COUNT    |
| Enter Agent test duration in seconds / 输入Agent测试持续时间(秒) | 100秒          |
| Enter Agent total test requests / 输入Agent测试请求总数      | 100次          |
| Enter Agent serialization method / 输入Agent序列化方式       | hessian2       |
| Agent Namespace / agent的命名空间(consumer和provide按照这个名字连接 / "consumer" and "provide" are connected according to this name) | dubbo-agent    |
| Enter number of Dubbo Produces (default: 10) / 输入Produce数量 | 10             |



###### 结果：会直接生成在当前目录生成文件包括

| 文件名称                                      | 文件解释                                                     |
| --------------------------------------------- | ------------------------------------------------------------ |
| fixed_count-consumer-dubbo-consumerCN/EN.html | cnusmer结果. 名称规则: 测试方法-测试方(consumer或者provide)-名称_CN/EN.html |
| consumer_resultsayHello.txt                   | json格式的测试结果. 名称规则:  测试方-result-方法名称        |
| provide_ressultprovider_1CN/EN.html           | provider的数据.  名称规则: provider-result-测试方的名称      |
| provide_resultprovider_1.txt                  | json格式的测试结果. 名称规则:  provider-result-测试方名称    |





#### 方式二:本地部署:

###### 克隆本项目到本地

**1.首先配置agent的参数**

| 参数名称                | 含义                | 是否必传                     |
| ----------------------- | ------------------- | ---------------------------- |
| SPRING_APPLICATION_NAME | agnet的名称         | 是                           |
| SERVICE_PORT            | agent的port         | 是                           |
| AGENT_DURATION_SECONDS  | agent测试的执行时间 | 否(默认:100)                 |
| AGENT_REQUEST_COUNT     | agent的次数         | 否(默认:100)                 |
| AGENT_LOCADBANCE        | 负载均衡的方式      | 是(默认: random)             |
| AGENT_SERIALIZATION     | 序列化的方式        | 是(默认:hessian2)            |
| AGENT_TEST_MODE         | 测试的方式          | 是(默认:FIXED_COUNT固定次数) |
|                         |                     |                              |

举例子: java java -DSPRING_APPLICATION_NAME=dubbo-agent -DSERVICE_PORT=8802  -jar

##### 2.如何配置自己的测试方法

##### Consumer 端配置

- ###### 首先在需要测试的 Consumer 上添加注解：

```java
@EnableDubboTest(basePackages = {"com.dubbo.consumer", "com.dubbo.common"}, testModel = "consumer")
```

​	**basePackages**: api的包路径

​	**testModel**: 当前的测试模式是consumer就写consumer，如果即是consumer也是provide就不用写

- 在抽象 API 上添加注解：

```java
 @DubboInvokeStat(namespace = "agentname",  argKey = "AGENT_NAME_HELLO" ,argValue= DubboInvokeEnum.class) 
```
**namespace** : 这次测试的agent名称

**argKey** : 你枚举对于的key的名称

**argValue** : 对应的测试数据枚举

##### Mock数据枚举类（你测试数据写的地方）
###### 重点：key是要测试的mock数据名称，里面是对应的值
```java
package com.dubbo.common.constant;
import com.alibaba.fastjson2.JSON;


public enum DubboInvokeEnum  {
    AGENT_NAME_HELLO(new DubboTest("你好")),
    AGENT_NAME_HELLO2(new DubboTest("你好号2")),
    AGENT_OTHER(new DubboTest("扩展测试"));
    private Object value;

    DubboInvokeEnum(Object reqObj) {
        this.value = reqObj;
    }
    public Object getValue() {
        return value;
    }
}
```
##### DubboTest（测试数据需要接受的参数）
###### 要求你这个参数要和要调用的方法名称一样
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
##### Provider 端配置

- ###### 首先在需要测试的 Provider 上添加注解：

```java
@EnableDubboTest(basePackages = {"com.dubbo.consumer", "com.dubbo.common"}, testModel = "provider")
```

​	**basePackages**: api的包路径

​	**testModel**: 当前的测试模式是provider就写provider，如果即是consumer也是provide就不用写