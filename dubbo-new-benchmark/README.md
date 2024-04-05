# Dubbo 自动化benchmark

## 我们的方案

| 指标名称                                                 | 单位       | 参与对象                                                      |
|------------------------------------------------------|----------|-----------------------------------------------------------|
| Unary ping pong median latency - Language comparison | microsec | Dubbo, Dubbo triple, GRPC, SpringCloud(OpenFeign), Thrift |
| Streaming ping pong median latency                   | microsec | Dubbo triple, GRPC                                        |
| Unary throughput QPS                                 |          | Dubbo, Dubbo triple, GRPC, SpringCloud(OpenFeign), Thrift |
| Unary server CPU                                     |          | Dubbo, Dubbo triple, GRPC, SpringCloud(OpenFeign), Thrift |
| Unary client CPU                                     |          | Dubbo, Dubbo triple, GRPC, SpringCloud(OpenFeign), Thrift |
| Streaming throughput QPS                             |          | Dubbo triple, GRPC                                        |
| Streaming server CPU                                 |          | Dubbo triple, GRPC                                        |
| Streaming client CPU                                 |          | Dubbo triple, GRPC                                        |
| Unary throughput QPS with 1MB message                |          | Dubbo triple, GRPC                                        |

## GRPC方案

[GRPC Benchmark 官方文档](https://grpc.io/docs/guides/benchmarking/)
[GRPC Benchmark 在线监控面板](https://grafana-dot-grpc-testing.appspot.com/?orgId=1)

avg ((sum by (pod) (irate(container_cpu_usage_seconds_total{container!="",container="grpc-server"}[5m]))) / on(pod) sum by (pod) (kube_pod_container_resource_limits{resource="cpu",pod=~"grpc-server.*"})) * 100.00