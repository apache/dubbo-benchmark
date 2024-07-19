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
package org.apache.dubbo.benchmark.thirft;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import service.demo.HelloMessage;
import service.demo.HelloResponse;
import service.demo.HelloService;

import java.util.logging.Logger;

@Service
public class TriftService {
    private static final Logger logger = Logger.getLogger(TriftService.class.getName());

    @Value("${server.host}")
    private String host;

    @Value("${server.port}")
    private int port;


    public void sayHi(String hello) {
        System.out.println("URL+端口号"+host+port);
        try {
            TTransport transport = new TSocket(host, port);
            transport.open();
            TProtocol protocol = new TBinaryProtocol(transport);

            HelloService.Client client = new HelloService.Client(protocol);

            HelloMessage request = new HelloMessage();
            request.setMessage(hello);

            HelloResponse response = client.sayHello(request);
            transport.close();
        } catch (TException e) {
            e.printStackTrace();
        }
    }
}
