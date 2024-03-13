package org.apache.dubbo.benchmark;

import com.google.protobuf.util.Timestamps;
import org.apache.dubbo.benchmark.bean.DubboUserServiceGrpc;
import org.apache.dubbo.benchmark.bean.PagePB;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.ChainedOptionsBuilder;
import org.openjdk.jmh.runner.options.Options;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@State(Scope.Benchmark)
public class ClientGrpc {
    private static final int CONCURRENCY = 32;

    private final ClassPathXmlApplicationContext context;
    private final DubboUserServiceGrpc.IUserService userService;
    private final AtomicInteger counter = new AtomicInteger(0);

    public ClientGrpc() {
        context = new ClassPathXmlApplicationContext("consumer.xml");
        context.start();
        userService = (DubboUserServiceGrpc.IUserService) context.getBean("userService");
    }


    @TearDown
    public void close() throws IOException {
        context.close();
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
                .include(ClientGrpc.class.getSimpleName())
                .exclude(Client.class.getSimpleName())
                .exclude(ClientNativeGrpc.class.getSimpleName())
                .exclude(ClientPb.class.getSimpleName())
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
