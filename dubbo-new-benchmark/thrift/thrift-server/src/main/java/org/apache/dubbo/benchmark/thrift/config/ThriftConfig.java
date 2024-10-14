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
package org.apache.dubbo.benchmark.thrift.config;

import org.apache.dubbo.benchmark.thrift.service.HelloServiceImpl;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import service.demo.HelloService;

import javax.annotation.PostConstruct;
import javax.sound.sampled.Port;

@Component
public class ThriftConfig {

    @PostConstruct
    public void start() {
        try {
            TServerTransport serverTransport = new TServerSocket(40002);
            TServer server = new TSimpleServer(new TServer.Args(serverTransport).processor(new HelloService.Processor<>(new HelloServiceImpl())));
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
