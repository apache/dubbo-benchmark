# Dubbo 测试工具

https://img.shields.io/badge/build-passing-brightgreen

本项目专注于测试 Dubbo 框架的性能表现。通过 Docker 容器化技术，帮助开发者快速测试项目中负载均衡算法的实际使用情况。

## 如何运行测试（当Agent命名空间是agentProto是proto测试)

### 方式一：docker部署

#### 1.执行命令

bash

```
# 进入项目目录
cd dubbo-dt

# 添加执行权限
chmod +x dubbo-dt.sh

# 运行配置脚本（可以直接回车全部默认）
./dubbo-dt.sh

# 启动所有服务
docker compose up -d

#最后进入到结果文件目录里面
cd result
```

#### 2.配置参数（可选）

| 参数               | 描述                      | 默认值                                                       |
| :----------------- | :------------------------ | :----------------------------------------------------------- |
| Dubbo Consumer数量 | 输入Consumer数量          | 1                                                            |
| Agent协议方式      | 输入agent的协议           | dubbo                                                        |
| Agent负载均衡策略  | 输入Agent负载均衡策略     | ConsistentHash                                               |
| Agent测试模式      | 输入Agent的测试方法       | FIXED_COUN(默认固定模式) 比如：SELFFUNCTION(自测模式) DURATION(持续时间模式) |
| Agent测试时长      | 输入Agent测试持续时间(秒) | 100秒(如果需要测试自己的方法，就必须加这个)                  |
| Agent测试请求数    | 输入Agent测试请求总数     | 100次                                                        |
| Agent序列化方式    | 输入Agent序列化方式       | hessian2                                                     |
| Agent命名空间      | agent的命名空间           | **dubbo-agent(无proto测试)，agentProto(带proto测试)**        |
| Dubbo Provider数量 | 输入Provider数量          | 10                                                           |

#### 3.生成结果（可选）

| 文件名称                                      | 描述               |
| :-------------------------------------------- | :----------------- |
| fixed_count-consumer-dubbo-consumerCN/EN.html | consumer结果       |
| consumer_result_sayHello.txt                  | json格式的测试结果 |
| provider_result_provider_1CN/EN.html          | provider的数据     |
| provider_result_provider_1.txt                | json格式的测试结果 |

### 方式二：本地部署

#### 1.配置agent参数

| 参数                    | 含义         | 必填 | 默认值      |
| :---------------------- | :----------- | :--- | :---------- |
| SPRING_APPLICATION_NAME | agent名称    | 是   | 无          |
| SERVICE_PORT            | agent端口    | 是   | 无          |
| AGENT_DURATION_SECONDS  | 测试执行时间 | 否   | 100         |
| AGENT_REQUEST_COUNT     | 测试次数     | 否   | 100         |
| AGENT_LOADBALANCE       | 负载均衡方式 | 否   | random      |
| AGENT_SERIALIZATION     | 序列化方式   | 否   | hessian2    |
| AGENT_PROTOCOL          | 协议方式     | 否   | dubbo       |
| AGENT_TEST_MODE         | 测试方式     | 否   | FIXED_COUNT |

启动示例：

```bash
java -DSPRING_APPLICATION_NAME=dubbo-agent -DSERVICE_PORT=8802 -jar
```



#### 2.配置测试方法

**Consumer端配置**

在Consumer上添加注解：

```java
@EnableDubboTest(basePackages = {"com.dubbo.common"}, testModel = "consumer")
```

| 参数名称     | 说明             | 举例                  |
| ------------ | ---------------- | --------------------- |
| basePackages | 抽象对象的包路径 | com.dubbo.common      |
| testModel    | 当前服务是哪一方 | consumer/provider/all |

在抽象API上添加注解：

```java
@DubboInvokeStat(namespace = "agentname", argKey = "AGENT_NAME_HELLO", argValue = DubboInvokeEnum.class)
```

| 参数名称  | 说明                      | 举例                  | 是否必要 |
| --------- | ------------------------- | --------------------- | -------- |
| namespace | 当前agent的名称可以多启动 | agentname             | 是       |
| argKey    | mock数据的key             | AGENT_NAME_HELLO      | 是       |
| argValue  | mock数据的储存对象        | DubboInvokeEnum.class | 是       |

**Mock数据枚举类**

```java
public enum DubboInvokeEnum {
    AGENT_NAME_HELLO("你好"),
    AGENT_NAME_HELLO2("你好2"),
    AGENT_OTHER("扩展测试"),
    AGENT_PROTO(UserProto.UserRequest.newBuilder()
            .setName("你好")
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



**Provider端配置**

在Provider上添加注解：

```java
@EnableDubboTest(basePackages = {"com.dubbo.common"}, testModel = "provider")
```

| 参数名称     | 说明             | 举例                  |
| ------------ | ---------------- | --------------------- |
| basePackages | 抽象对象的包路径 | com.dubbo.common      |
| testModel    | 当前服务是哪一方 | consumer/provider/all |

### 协议转换:

> ***不同的协议使用不同的方法创建，只要抽象对象上面可以加到@DubboInvokeStat注解，启动类上面有@EnableDubboTest注解，就可以使用基测工具。<!--记住使用protobuf的时候先使用maven编译源文件，再启动-->***



## 结果：Dubbo性能测试结果文件命名规则说明

| 文件类别               | 文件前缀               | 中间部分       | 后缀部分 | 语言标识  | 文件格式 | 说明                                                     |
| :--------------------- | :--------------------- | :------------- | :------- | :-------- | :------- | :------------------------------------------------------- |
| **消费者测试详细结果** | `consumer-result-`     | `{测试方法名}` | -        | -         | `.txt`   | 记录特定方法的每次调用详情，包括响应时间、状态等原始数据 |
| **提供者测试详细结果** | `provide-result-`      | `{服务名称}`   | -        | -         | `.txt`   | 记录特定提供者服务的原始测试数据，包括调用统计和性能指标 |
| **消费者测试报告**     | `{测试模式}-consumer-` | `{服务名称}`   | -        | `CN`/`EN` | `.html`  | 消费者服务的可视化测试报告，包含性能图表和统计摘要       |
| **提供者趋势分析报告** | `provide-ressult-`     | `{服务名称}`   | -        | `CN`/`EN` | `.html`  | 提供者服务的调用趋势分析报告，展示时间序列性能数据       |

