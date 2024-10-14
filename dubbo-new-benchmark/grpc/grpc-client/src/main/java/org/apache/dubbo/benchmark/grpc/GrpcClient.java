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
package org.apache.dubbo.benchmark.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class GrpcClient {
    private GreeterGrpc.GreeterBlockingStub greeterBlockingStub;

    @Value("${server.url}")
    private String url;

    // 构造请求
    private HelloRequest convertRequest (String name, String sex) {
        HelloRequest helloRequest = HelloRequest.newBuilder()
                .setName(name)
                .setSex(sex)
                .build();
        return helloRequest;
    }

    public String sayHello (String name, String sex) {
        HelloReply reply = greeterBlockingStub.sayHello(convertRequest(name,sex));
        System.out.println("接收到服务端返回结果: " + reply.getMessage());
        return reply.getMessage();
    }

    @PostConstruct
    private void init() {
        System.out.println("服务端URL"+url);
        ManagedChannel managedChannel = ManagedChannelBuilder.forTarget(url)
                .usePlaintext()
                .build();
        greeterBlockingStub  = GreeterGrpc.newBlockingStub(managedChannel);
        System.out.println("链接server ==================");
    }
}
