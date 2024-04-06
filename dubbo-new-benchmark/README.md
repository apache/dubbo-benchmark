# Dubbo 自动化benchmark

## 我们的方案

|                       指标名称                       | 单位     |                         参与对象                          |
| :--------------------------------------------------: | -------- | :-------------------------------------------------------: |
| Unary ping pong median latency - Language comparison | microsec | Dubbo, Dubbo triple, GRPC, SpringCloud(OpenFeign), Thrift |
|          Streaming ping pong median latency          | microsec |                    Dubbo triple, GRPC                     |
|                 Unary throughput QPS                 |          | Dubbo, Dubbo triple, GRPC, SpringCloud(OpenFeign), Thrift |
|                   Unary server CPU                   |          | Dubbo, Dubbo triple, GRPC, SpringCloud(OpenFeign), Thrift |
|                   Unary client CPU                   |          | Dubbo, Dubbo triple, GRPC, SpringCloud(OpenFeign), Thrift |
|               Streaming throughput QPS               |          |                    Dubbo triple, GRPC                     |
|                 Streaming server CPU                 |          |                    Dubbo triple, GRPC                     |
|                 Streaming client CPU                 |          |                    Dubbo triple, GRPC                     |
|        Unary throughput QPS with 1MB message         |          |                    Dubbo triple, GRPC                     |

## GRPC方案

[GRPC Benchmark 官方文档](https://grpc.io/docs/guides/benchmarking/)
[GRPC Benchmark 在线监控面板](https://grafana-dot-grpc-testing.appspot.com/?orgId=1)

avg ((sum by (pod) (irate(container_cpu_usage_seconds_total{container!="",container="grpc-server"}[5m]))) / on(pod) sum by (pod) (kube_pod_container_resource_limits{resource="cpu",pod=~"grpc-server.*"})) * 100.00

## 使用方法

### 1.目录结构

![image-20240405173640734](C:\Users\wys\AppData\Roaming\Typora\typora-user-images\image-20240405173640734.png)

![image-20240405174228197](C:\Users\wys\AppData\Roaming\Typora\typora-user-images\image-20240405174228197.png)



|       模块名       |           模块作用           |
| :----------------: | :--------------------------: |
|   benchmark-base   |        自动化压测模块        |
| dubbo-dubbo-client |       dubbo协议客户端        |
| dubbo-dubbo-server |       dubbo协议服务端        |
|       deploy       |           部署文件           |
|     deploy/app     | 部署定时任务和协议通信server |
|   deploy/monitor   |    部署prometheus采集数据    |
|    deploy/nacos    |          部署nacos           |
| deploy/postgresql  |        部署postgresql        |
|      ns.yaml       | 创建dubbo-benchmark命名空间  |
|        sql         |       数据库初始化文件       |

### 2.模块打包上传

<img src="C:\Users\wys\AppData\Roaming\Typora\typora-user-images\image-20240405180318200.png" alt="image-20240405180318200" style="zoom: 67%;" />

1. 先对dubbo-base-benchmark进行打包

2. 对各个模块可以选择版本号进行打包，对client和server进行分别打包

   ![image-20240405180512533](C:\Users\wys\AppData\Roaming\Typora\typora-user-images\image-20240405180512533.png)

3. 对特定版本进行部署，需要指定版本号

![image-20240405181143377](C:\Users\wys\AppData\Roaming\Typora\typora-user-images\image-20240405181143377.png)

### 3.自动化部署

1. 首先创建dubbo-benchmark的namespace，然后对postgresql和nacos进行部署。

2. 创建monitor的namespace，执行install.sh

   ![image-20240405175410906](C:\Users\wys\AppData\Roaming\Typora\typora-user-images\image-20240405175410906.png)

3. 对各个app里面的内容，先部署server，然后在部署定时任务。

   ![image-20240405175529359](C:\Users\wys\AppData\Roaming\Typora\typora-user-images\image-20240405175529359.png)

4. 将部署的grafana进行本地端口映射

   ![95c1d57b117a050f83ef77c8be4e524](D:\微信文件\WeChat Files\wxid_ezbercj1ubpj22\FileStorage\Temp\95c1d57b117a050f83ef77c8be4e524.jpg)

   
