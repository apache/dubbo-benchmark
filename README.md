# Dubbo Benchmark

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

## Run with docker
```bash
docker run -v "$PWD:/tmp" openjdk java -server -Xmx1g -Xms1g -XX:MaxDirectMemorySize=1g -XX:+UseG1GC -jar /tmp/dubbo-kryo-server/target/dubbo-kryo-server-1.0-SNAPSHOT.jar
docker run  -v "$PWD:/tmp" -e server.host=172.17.0.3 openjdk java -server -Xmx1g -Xms1g -XX:MaxDirectMemorySize=1g -XX:+UseG1GC -jar /tmp/dubbo-kryo-client/target/dubbo-kryo-client-1.0-SNAPSHOT.jar
```