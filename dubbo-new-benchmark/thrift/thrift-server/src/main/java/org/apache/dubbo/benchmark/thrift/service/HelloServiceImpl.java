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
package org.apache.dubbo.benchmark.thrift.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import service.demo.HelloMessage;
import service.demo.HelloResponse;
import service.demo.HelloService;

@Slf4j
public class HelloServiceImpl implements HelloService.Iface {

    @Override
    public HelloResponse sayHello(HelloMessage request) throws TException {
        String message = request.getMessage();

        HelloResponse response = new HelloResponse();
        response.setMessage("Hello " + message);
        return response;
    }
}
