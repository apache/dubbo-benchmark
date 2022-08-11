# Dubbo Benchmark

[![Build Status](https://travis-ci.org/apache/dubbo-benchmark.svg?branch=master)](https://travis-ci.org/apache/dubbo-benchmark)

This project focuses on benchmarking and profiling dubbo framework with the combination of different serialization and transporter options. The code and the idea behinds it is inspired by [RPC Benchmark](https://github.com/hank-whu/rpc-benchmark). 

## How To Run Benchmark 

Clone this project onto your desktop, then

* Start the target server first, for example:
```bash
./benchmark.sh dubbo-kryo-server
```

* Start the corresponding client, for example:
```bash
./benchmark.sh dubbo-kryo-client
```

* If you want run dubbo-go benchmark, use the script `benchmark-go.sh`, for example:
```bash
./benchmark-go.sh dubbo-go-triple-server
./benchmark-go.sh dubbo-go-triple-client

#You can use -h to see the configurable parameters, for example:
./benchmark-go.sh dubbo-go-triple-client -h
```

## How to Run Profiling

* Start the target server in profiling mode, for example:
```bash
./benchmark.sh -m profiling dubbo-kryo-server
```

* Start the corresponding client, for example:
```bash
./benchmark.sh dubbo-kryo-client
```

## Specify hostname, port and output file for service

```bash
./benchmark.sh -s [hostname|ip address] -p port -f output
```
