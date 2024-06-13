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
package org.apache.dubbo.benchmark.triple;

import org.apache.dubbo.benchmark.TestDriver;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.tri.unary.Greeter;
import org.apache.dubbo.tri.unary.GreeterReply;
import org.apache.dubbo.tri.unary.GreeterRequest;
import org.openjdk.jmh.runner.RunnerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class Task {
    private static final Logger LOGGER = LoggerFactory.getLogger(Task.class);
    @DubboReference
    private Greeter greeter;

    public void runTask() {
        TestDriver testDriver = new TestDriver();
        try {
            testDriver.runBenchmark(() -> {
                GreeterReply result = greeter.greet(GreeterRequest.newBuilder().setName("dubbo").build());
                LOGGER.info("Receive result ======> {}", result.getMessage());
            });
        } catch (RunnerException e) {
            LOGGER.error("Error", e);
        }

        ConfigurableApplicationContext context = ClientApplication.context;
        if (context != null) {
            context.close();
        } else {
            LOGGER.error("context is null");
        }
    }
}
