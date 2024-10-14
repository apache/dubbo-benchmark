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
package org.apache.dubbo.benchmark.config;

import javax.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Validated
@Data
@ConfigurationProperties(prefix = "benchmark")
@Configuration
public class TestConfig {

    private Integer testThreads = Runtime.getRuntime().availableProcessors() * 2;

    private Integer warmupIterations = 3;

    private Integer warmupTimeSeconds = 10;

    private Integer measurementIterations = 3;

    private Integer measurementTime = 10;

    private Integer timeoutSeconds = 600;

    @NotEmpty(message = "benchmark.protocol is required")
    private String protocol;

    @NotEmpty(message = "benchmark.rpc-version is required")
    private String rpcVersion;

    @NotEmpty(message = "benchmark.prometheus-url is required")
    private String prometheusUrl;
}
