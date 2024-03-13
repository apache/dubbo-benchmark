/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.benchmark;

import io.grpc.Server;
import io.grpc.netty.NettyServerBuilder;
import org.apache.dubbo.benchmark.service.NativeGrpcUserServiceImpl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

public class ServerNativeGrpc {

    public static void main(String[] args) throws InterruptedException, IOException {
        Properties properties = loadProperties();
        int port = Integer.parseInt(properties.getProperty("server.port"));
        Server server = startServer(port);
        System.out.println("Native Grpc Server started, listening on " + port);
        server.awaitTermination();
    }

    private static Properties loadProperties() throws IOException {
        try (InputStream stream = ServerNativeGrpc.class.getClassLoader().getResourceAsStream("benchmark.properties")) {
            InputStream propertiesStream = Objects.requireNonNull(stream, "benchmark.properties not found");
            Properties properties = new Properties();
            properties.load(propertiesStream);
            return properties;
        }
    }

    private static Server startServer(int port) throws IOException {
        Server server = NettyServerBuilder.forPort(port)
                .addService(new NativeGrpcUserServiceImpl())
                .build();
        server.start();
        return server;
    }
}
