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
package org.apache.dubbo.benchmark.utils;

public class BenchmarkTypeHelper {

    public static String getServerContainerByProtocol(String protocol) {
        switch (protocol) {
            case "dubbo":
                return "dubbo-dubbo-server";
            case "triple":
                return "dubbo-triple-server";
            case "grpc":
                return "grpc-server";
            case "openFeign":
                return "springcloud-server";
            case "thrift":
                return "thrift-server";
            default:
                throw new RuntimeException("Unsupported protocol: " + protocol);
        }
    }

    public static String getClientContainerByProtocol(String protocol) {
        switch (protocol) {
            case "dubbo":
                return "dubbo-dubbo-client";
            case "triple":
                return "dubbo-triple-client";
            case "grpc":
                return "grpc-client";
            case "openFeign":
                return "springcloud-client";
            case "thrift":
                return "thrift-client";
            default:
                throw new RuntimeException("Unsupported protocol: " + protocol);
        }
    }
}
