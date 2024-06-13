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

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.benchmark.config.TestConfig;
import org.apache.dubbo.benchmark.utils.SpringContextUtils;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.ChainedOptionsBuilder;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

@Slf4j
@State(Scope.Benchmark)
public class TestDriver {

    private static LocalDateTime startTime;
    private static LocalDateTime endTime;
    private final TestConfig config;
    private final ResultService resultService;

    public TestDriver() {
        this.config = SpringContextUtils.getBean(TestConfig.class);
        this.resultService = SpringContextUtils.getBean(ResultService.class);
    }

    private static ChainedOptionsBuilder doOptions(ChainedOptionsBuilder optBuilder) {
        String output = System.getProperty("benchmark.output");
        if (output != null && !output.trim().isEmpty()) {
            optBuilder.output(output);
        }
        return optBuilder;
    }

    @Setup
    public void setup() {
        startTime = LocalDateTime.now();
    }

    @Benchmark
    @BenchmarkMode({Mode.Throughput, Mode.AverageTime, Mode.SampleTime})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void executeCall() {
        Runnable rpcCall = RpcCall.getRpcCall();
        rpcCall.run();
    }

    public void runBenchmark(Runnable rpcCall) throws RunnerException {
        RpcCall.setRpcCall(rpcCall);
        log.info("RpcCall content: {}", RpcCall.getRpcCall());

        int warmupIterations = config.getWarmupIterations();
        int warmupTime = config.getWarmupTimeSeconds();
        int measurementIterations = config.getMeasurementIterations();
        int measurementTime = config.getMeasurementTime();

        ChainedOptionsBuilder optBuilder = new OptionsBuilder()
            .include(TestDriver.class.getSimpleName())
            .warmupIterations(warmupIterations)
            .warmupTime(TimeValue.seconds(warmupTime))
            .measurementIterations(measurementIterations)
            .measurementTime(TimeValue.seconds(measurementTime))
            .threads(config.getTestThreads())
            .timeUnit(TimeUnit.MILLISECONDS)
            .forks(0)
            .shouldDoGC(true)
            .timeout(TimeValue.seconds(config.getTimeoutSeconds()));

        Options opt = doOptions(optBuilder).build();

        Collection<RunResult> results = new Runner(opt).run();

        endTime = LocalDateTime.now();
        log.info("Benchmark is end. Start time: {}, End time: {}", startTime, endTime);
        resultService.saveJmhResults(results);

        try {
            Thread.sleep(1000 * 60);
        } catch (InterruptedException e) {
            log.error("Thread sleep error", e);
        }
        endTime = endTime.plusSeconds(30);
        resultService.saveCpuResults(startTime, endTime);
        resultService.saveMemResults(startTime, endTime);
    }

}