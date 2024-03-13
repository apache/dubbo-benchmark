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

import com.google.protobuf.util.Timestamps;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.apache.dubbo.benchmark.bean.PagePB;
import org.apache.dubbo.benchmark.bean.UserServiceGrpc;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.ChainedOptionsBuilder;
import org.openjdk.jmh.runner.options.Options;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@State(Scope.Benchmark)
public class ClientNativeGrpc {

    private static final int CONCURRENCY = 32;

    private final UserServiceGrpc.UserServiceBlockingStub userService;

    private final AtomicInteger counter = new AtomicInteger();

    public ClientNativeGrpc() {
        try (InputStream stream = ClientNativeGrpc.class.getClassLoader().getResourceAsStream("benchmark.properties")) {
            InputStream propertiesStream = Objects.requireNonNull(stream, "benchmark.properties not found");
            Properties properties = new Properties();
            properties.load(propertiesStream);
            ManagedChannel channel = ManagedChannelBuilder.forAddress(properties.getProperty("server.host"),
                            Integer.parseInt(properties.getProperty("server.port")))
                    .usePlaintext()
                    .build();
            this.userService = UserServiceGrpc.newBlockingStub(channel);
        } catch (Throwable throwable) {
            throw new IllegalStateException(throwable);
        }

    }

    @Benchmark
    public boolean existUser() throws Exception {
        final int count = counter.getAndIncrement();
        return userService.existUser(PagePB.Request.newBuilder().setEmail(String.valueOf(count)).build())
                .getState();
    }

    @Benchmark
    public boolean createUser() throws Exception {
        final int count = counter.getAndIncrement();

        final PagePB.User.Builder user = PagePB.User.newBuilder();
        user.setId(count);
        user.setName(new String("Doug Lea"));
        user.setSex(1);
        user.setBirthday(Timestamps.fromMillis(System.currentTimeMillis()));
        user.setEmail(new String("dong.lea@gmail.com"));
        user.setMobile(new String("18612345678"));
        user.setAddress(new String("北京市 中关村 中关村大街1号 鼎好大厦 1605"));
        user.setIcon(new String("https://www.baidu.com/img/bd_logo1.png"));
        user.setStatus(1);
        user.setCreateTime(Timestamps.fromMillis(System.currentTimeMillis()));
        user.setUpdateTime(user.getCreateTime());
        List<Integer> permissions = new ArrayList<Integer>(
                Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 19, 88, 86, 89, 90, 91, 92));
        user.addAllPermissions(permissions);
        final PagePB.Request.Builder builder = PagePB.Request.newBuilder();
        return userService.createUser(builder.setUser(user.build()).build()).getState();

    }

    @Benchmark
    public PagePB.User getUser() throws Exception {
        final int count = counter.getAndIncrement();
        return userService.getUser(PagePB.Request.newBuilder().setId(count).build()).getUser();
    }

    @Benchmark
    public PagePB.Page listUser() throws Exception {
        final int count = counter.getAndIncrement();
        return userService.listUser(PagePB.Request.newBuilder().setPage(count).build()).getPage();
    }

    public static void main(String[] args) throws Exception {
        ClientHelper.Arguments arguments = ClientHelper.parseArguments(args);
        String format = arguments.getResultFormat();
        ChainedOptionsBuilder optBuilder = ClientHelper.newBaseChainedOptionsBuilder(arguments)
                .result(System.currentTimeMillis() + "." + format)
                .include(ClientNativeGrpc.class.getSimpleName())
                .mode(Mode.Throughput)
                .mode(Mode.AverageTime)
                .mode(Mode.SampleTime)
                .timeUnit(TimeUnit.MILLISECONDS)
                .threads(CONCURRENCY)
                .forks(1);
        Options opt = optBuilder.build();
        new Runner(opt).run();
    }
}
