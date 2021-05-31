package org.apache.dubbo.benchmark;

import org.apache.dubbo.benchmark.bean.PagePB;
import org.apache.dubbo.benchmark.bean.UserServiceDubbo;
import com.google.protobuf.util.Timestamps;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.ChainedOptionsBuilder;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@State(Scope.Benchmark)
public class ClientPb {
    private static final int CONCURRENCY = 32;

    private final ClassPathXmlApplicationContext context;
    private final UserServiceDubbo.IUserService userService;
    private final AtomicInteger counter = new AtomicInteger(0);

    public ClientPb() {
        context = new ClassPathXmlApplicationContext("consumer.xml");
        context.start();
        userService = (UserServiceDubbo.IUserService) context.getBean("userService");
    }


    @TearDown
    public void close() throws IOException {
        context.close();
    }

    @Benchmark
    @BenchmarkMode({Mode.Throughput, Mode.AverageTime, Mode.SampleTime})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public boolean existUser() throws Exception {
        final int count = counter.getAndIncrement();
        return userService.existUser(PagePB.Request.newBuilder().setEmail(String.valueOf(count)).build())
                .getState();
    }

    @Benchmark
    @BenchmarkMode({Mode.Throughput, Mode.AverageTime, Mode.SampleTime})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
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
    @BenchmarkMode({Mode.Throughput, Mode.AverageTime, Mode.SampleTime})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public PagePB.User getUser() throws Exception {
        final int count = counter.getAndIncrement();
        return userService.getUser(PagePB.Request.newBuilder().setId(count).build()).getUser();
    }

    @Benchmark
    @BenchmarkMode({Mode.Throughput, Mode.AverageTime, Mode.SampleTime})
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public PagePB.Page listUser() throws Exception {
        final int count = counter.getAndIncrement();
        return userService.listUser(PagePB.Request.newBuilder().setPage(count).build()).getPage();
    }

    public static void main(String[] args) throws Exception {
        Options opt;
        ChainedOptionsBuilder optBuilder = new OptionsBuilder()
                .include(ClientPb.class.getSimpleName())
                .warmupIterations(3)
                .warmupTime(TimeValue.seconds(10))
                .measurementIterations(3)
                .measurementTime(TimeValue.seconds(10))
                .threads(CONCURRENCY)
                .forks(1);

        opt = doOptions(optBuilder).build();

        new Runner(opt).run();
    }

    private static ChainedOptionsBuilder doOptions(ChainedOptionsBuilder optBuilder) {
        String output = System.getProperty("benchmark.output");
        if (output != null && !output.trim().isEmpty()) {
            optBuilder.output(output);
        }
        return optBuilder;
    }
}
