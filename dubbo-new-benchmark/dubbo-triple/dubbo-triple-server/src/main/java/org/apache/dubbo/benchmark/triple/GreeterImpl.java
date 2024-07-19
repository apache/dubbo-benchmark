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

import java.util.concurrent.CompletableFuture;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.tri.unary.DubboGreeterTriple;
import org.apache.dubbo.tri.unary.GreeterReply;
import org.apache.dubbo.tri.unary.GreeterRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DubboService
public class GreeterImpl extends DubboGreeterTriple.GreeterImplBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(GreeterImpl.class);

    @Override
    public GreeterReply greet(GreeterRequest request) {
        LOGGER.debug("Server received greet request {}", request);
        return GreeterReply.newBuilder()
            .setMessage("hello," + request.getName())
            .build();
    }

    public CompletableFuture<GreeterReply> greetAsync(GreeterRequest request) {
        return CompletableFuture.completedFuture(greet(request));
    }
}
