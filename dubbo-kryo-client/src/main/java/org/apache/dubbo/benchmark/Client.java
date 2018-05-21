package org.apache.dubbo.benchmark;

import com.alibaba.dubbo.config.ProtocolConfig;
import org.apache.dubbo.benchmark.bean.Page;
import org.apache.dubbo.benchmark.bean.User;
import org.apache.dubbo.benchmark.rpc.AbstractClient;
import org.apache.dubbo.benchmark.service.UserService;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class Client extends AbstractClient {
    private static final int CONCURRENCY = 32;

    private final ClassPathXmlApplicationContext context;
    private final UserService userService;

    public Client() {
        context = new ClassPathXmlApplicationContext("consumer.xml");
        context.start();
        userService = (UserService) context.getBean("userService");
    }

    @Override
    protected UserService getUserService() {
        return userService;
    }

    @TearDown
    public void close() throws IOException {
        context.close();
        ProtocolConfig.destroyAll();
    }

    @Benchmark
    @BenchmarkMode({Mode.Throughput, Mode.AverageTime, Mode.SampleTime})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Override
    public boolean existUser() throws Exception {
        return super.existUser();
    }

    @Benchmark
    @BenchmarkMode({Mode.Throughput, Mode.AverageTime, Mode.SampleTime})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Override
    public boolean createUser() throws Exception {
        return super.createUser();
    }

    @Benchmark
    @BenchmarkMode({Mode.Throughput, Mode.AverageTime, Mode.SampleTime})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Override
    public User getUser() throws Exception {
        return super.getUser();
    }

    @Benchmark
    @BenchmarkMode({Mode.Throughput, Mode.AverageTime, Mode.SampleTime})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Override
    public Page<User> listUser() throws Exception {
        return super.listUser();
    }

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(Client.class.getSimpleName())
                .warmupIterations(3)
                .warmupTime(TimeValue.seconds(10))
                .measurementIterations(3)
                .measurementTime(TimeValue.seconds(10))
                .threads(CONCURRENCY)
                .resultFormat(ResultFormatType.JSON)
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
