
    package org.apache.dubbo.benchmark.bean;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@javax.annotation.Generated(
value = "by Dubbo generator",
comments = "Source: Page.proto")
public final class UserServiceDubbo {
private static final AtomicBoolean registered = new AtomicBoolean();

private static Class<?> init() {
Class<?> clazz = null;
try {
clazz = Class.forName(UserServiceDubbo.class.getName());
if (registered.compareAndSet(false, true)) {
//    TODO: nowadays protobuf and grpc are not supprted in Dubbo3.x
//    org.apache.dubbo.common.serialize.protobuf.support.ProtobufUtils.marshaller(
//    org.apache.dubbo.benchmark.bean.PagePB.Response.getDefaultInstance());
//    org.apache.dubbo.common.serialize.protobuf.support.ProtobufUtils.marshaller(
//    org.apache.dubbo.benchmark.bean.PagePB.Request.getDefaultInstance());
}
} catch (ClassNotFoundException e) {
// ignore
}
return clazz;
}

private UserServiceDubbo() {}

public static final String SERVICE_NAME = "org.apache.dubbo.benchmark.bean.UserService";

/**
* Code generated for Dubbo
*/
public interface IUserService {

static Class<?> clazz = init();

    org.apache.dubbo.benchmark.bean.PagePB.Response existUser(org.apache.dubbo.benchmark.bean.PagePB.Request request);

    CompletableFuture<org.apache.dubbo.benchmark.bean.PagePB.Response> existUserAsync(org.apache.dubbo.benchmark.bean.PagePB.Request request);

    org.apache.dubbo.benchmark.bean.PagePB.Response createUser(org.apache.dubbo.benchmark.bean.PagePB.Request request);

    CompletableFuture<org.apache.dubbo.benchmark.bean.PagePB.Response> createUserAsync(org.apache.dubbo.benchmark.bean.PagePB.Request request);

    org.apache.dubbo.benchmark.bean.PagePB.Response getUser(org.apache.dubbo.benchmark.bean.PagePB.Request request);

    CompletableFuture<org.apache.dubbo.benchmark.bean.PagePB.Response> getUserAsync(org.apache.dubbo.benchmark.bean.PagePB.Request request);

    org.apache.dubbo.benchmark.bean.PagePB.Response listUser(org.apache.dubbo.benchmark.bean.PagePB.Request request);

    CompletableFuture<org.apache.dubbo.benchmark.bean.PagePB.Response> listUserAsync(org.apache.dubbo.benchmark.bean.PagePB.Request request);


}

}
