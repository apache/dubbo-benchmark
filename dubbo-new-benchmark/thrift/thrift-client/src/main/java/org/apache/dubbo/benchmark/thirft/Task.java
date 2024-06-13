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

import org.apache.dubbo.benchmark.TestDriver;
import org.openjdk.jmh.runner.RunnerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class Task {
    private static final Logger LOGGER = LoggerFactory.getLogger(Task.class);

    @Autowired
    private TriftService triftService;

    public void runTask(ConfigurableApplicationContext context) {
        TestDriver testDriver = new TestDriver();
        try {
            testDriver.runBenchmark(() -> {
                triftService.sayHi("thrift");
            });
        } catch (RunnerException e) {
            LOGGER.error("Error", e);
        }
        if (context != null) {
            context.close();
        } else {
            LOGGER.error("context is null");
        }
    }
}
