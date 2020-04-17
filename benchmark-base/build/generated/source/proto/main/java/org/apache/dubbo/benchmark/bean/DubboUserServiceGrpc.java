
    package org.apache.dubbo.benchmark.bean;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.config.ReferenceConfigBase;

import java.util.concurrent.TimeUnit;

import static org.apache.dubbo.common.constants.CommonConstants.DEFAULT_TIMEOUT;
import static org.apache.dubbo.common.constants.CommonConstants.TIMEOUT_KEY;

import static org.apache.dubbo.benchmark.bean.UserServiceGrpc.getServiceDescriptor;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

@javax.annotation.Generated(
value = "by DubboGrpc generator",
comments = "Source: Page.proto")
public final class DubboUserServiceGrpc {
private DubboUserServiceGrpc() {}

public static class DubboUserServiceStub implements IUserService {

protected URL url;
protected ReferenceConfigBase<?> referenceConfig;

protected UserServiceGrpc.UserServiceBlockingStub blockingStub;
protected UserServiceGrpc.UserServiceFutureStub futureStub;
protected UserServiceGrpc.UserServiceStub stub;

public DubboUserServiceStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions, URL url, ReferenceConfigBase<?> referenceConfig) {
this.url = url;
this.referenceConfig = referenceConfig;

blockingStub = UserServiceGrpc.newBlockingStub(channel).build(channel, callOptions);
futureStub = UserServiceGrpc.newFutureStub(channel).build(channel, callOptions);
stub = UserServiceGrpc.newStub(channel).build(channel, callOptions);
}

    public org.apache.dubbo.benchmark.bean.PagePB.Response existUser(org.apache.dubbo.benchmark.bean.PagePB.Request request) {
    return blockingStub
    .withDeadlineAfter(url.getParameter(TIMEOUT_KEY, DEFAULT_TIMEOUT), TimeUnit.MILLISECONDS)
    .existUser(request);
    }

    public com.google.common.util.concurrent.ListenableFuture<org.apache.dubbo.benchmark.bean.PagePB.Response> existUserAsync(org.apache.dubbo.benchmark.bean.PagePB.Request request) {
    return futureStub
    .withDeadlineAfter(url.getParameter(TIMEOUT_KEY, DEFAULT_TIMEOUT), TimeUnit.MILLISECONDS)
    .existUser(request);
    }

    public void existUser(org.apache.dubbo.benchmark.bean.PagePB.Request request, io.grpc.stub.StreamObserver<org.apache.dubbo.benchmark.bean.PagePB.Response> responseObserver){
    stub
    .withDeadlineAfter(url.getParameter(TIMEOUT_KEY, DEFAULT_TIMEOUT), TimeUnit.MILLISECONDS)
    .existUser(request, responseObserver);
    }

    public org.apache.dubbo.benchmark.bean.PagePB.Response createUser(org.apache.dubbo.benchmark.bean.PagePB.Request request) {
    return blockingStub
    .withDeadlineAfter(url.getParameter(TIMEOUT_KEY, DEFAULT_TIMEOUT), TimeUnit.MILLISECONDS)
    .createUser(request);
    }

    public com.google.common.util.concurrent.ListenableFuture<org.apache.dubbo.benchmark.bean.PagePB.Response> createUserAsync(org.apache.dubbo.benchmark.bean.PagePB.Request request) {
    return futureStub
    .withDeadlineAfter(url.getParameter(TIMEOUT_KEY, DEFAULT_TIMEOUT), TimeUnit.MILLISECONDS)
    .createUser(request);
    }

    public void createUser(org.apache.dubbo.benchmark.bean.PagePB.Request request, io.grpc.stub.StreamObserver<org.apache.dubbo.benchmark.bean.PagePB.Response> responseObserver){
    stub
    .withDeadlineAfter(url.getParameter(TIMEOUT_KEY, DEFAULT_TIMEOUT), TimeUnit.MILLISECONDS)
    .createUser(request, responseObserver);
    }

    public org.apache.dubbo.benchmark.bean.PagePB.Response getUser(org.apache.dubbo.benchmark.bean.PagePB.Request request) {
    return blockingStub
    .withDeadlineAfter(url.getParameter(TIMEOUT_KEY, DEFAULT_TIMEOUT), TimeUnit.MILLISECONDS)
    .getUser(request);
    }

    public com.google.common.util.concurrent.ListenableFuture<org.apache.dubbo.benchmark.bean.PagePB.Response> getUserAsync(org.apache.dubbo.benchmark.bean.PagePB.Request request) {
    return futureStub
    .withDeadlineAfter(url.getParameter(TIMEOUT_KEY, DEFAULT_TIMEOUT), TimeUnit.MILLISECONDS)
    .getUser(request);
    }

    public void getUser(org.apache.dubbo.benchmark.bean.PagePB.Request request, io.grpc.stub.StreamObserver<org.apache.dubbo.benchmark.bean.PagePB.Response> responseObserver){
    stub
    .withDeadlineAfter(url.getParameter(TIMEOUT_KEY, DEFAULT_TIMEOUT), TimeUnit.MILLISECONDS)
    .getUser(request, responseObserver);
    }

    public org.apache.dubbo.benchmark.bean.PagePB.Response listUser(org.apache.dubbo.benchmark.bean.PagePB.Request request) {
    return blockingStub
    .withDeadlineAfter(url.getParameter(TIMEOUT_KEY, DEFAULT_TIMEOUT), TimeUnit.MILLISECONDS)
    .listUser(request);
    }

    public com.google.common.util.concurrent.ListenableFuture<org.apache.dubbo.benchmark.bean.PagePB.Response> listUserAsync(org.apache.dubbo.benchmark.bean.PagePB.Request request) {
    return futureStub
    .withDeadlineAfter(url.getParameter(TIMEOUT_KEY, DEFAULT_TIMEOUT), TimeUnit.MILLISECONDS)
    .listUser(request);
    }

    public void listUser(org.apache.dubbo.benchmark.bean.PagePB.Request request, io.grpc.stub.StreamObserver<org.apache.dubbo.benchmark.bean.PagePB.Response> responseObserver){
    stub
    .withDeadlineAfter(url.getParameter(TIMEOUT_KEY, DEFAULT_TIMEOUT), TimeUnit.MILLISECONDS)
    .listUser(request, responseObserver);
    }

}

public static DubboUserServiceStub getDubboStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions, URL url, ReferenceConfigBase<?> referenceConfig) {
return new DubboUserServiceStub(channel, callOptions, url, referenceConfig);
}

public interface IUserService {
    default public org.apache.dubbo.benchmark.bean.PagePB.Response existUser(org.apache.dubbo.benchmark.bean.PagePB.Request request) {
    throw new UnsupportedOperationException("No need to override this method, extend XxxImplBase and override all methods it allows.");
    }

    default public com.google.common.util.concurrent.ListenableFuture<org.apache.dubbo.benchmark.bean.PagePB.Response> existUserAsync(org.apache.dubbo.benchmark.bean.PagePB.Request request) {
    throw new UnsupportedOperationException("No need to override this method, extend XxxImplBase and override all methods it allows.");
    }

    public void existUser(org.apache.dubbo.benchmark.bean.PagePB.Request request, io.grpc.stub.StreamObserver<org.apache.dubbo.benchmark.bean.PagePB.Response> responseObserver);

    default public org.apache.dubbo.benchmark.bean.PagePB.Response createUser(org.apache.dubbo.benchmark.bean.PagePB.Request request) {
    throw new UnsupportedOperationException("No need to override this method, extend XxxImplBase and override all methods it allows.");
    }

    default public com.google.common.util.concurrent.ListenableFuture<org.apache.dubbo.benchmark.bean.PagePB.Response> createUserAsync(org.apache.dubbo.benchmark.bean.PagePB.Request request) {
    throw new UnsupportedOperationException("No need to override this method, extend XxxImplBase and override all methods it allows.");
    }

    public void createUser(org.apache.dubbo.benchmark.bean.PagePB.Request request, io.grpc.stub.StreamObserver<org.apache.dubbo.benchmark.bean.PagePB.Response> responseObserver);

    default public org.apache.dubbo.benchmark.bean.PagePB.Response getUser(org.apache.dubbo.benchmark.bean.PagePB.Request request) {
    throw new UnsupportedOperationException("No need to override this method, extend XxxImplBase and override all methods it allows.");
    }

    default public com.google.common.util.concurrent.ListenableFuture<org.apache.dubbo.benchmark.bean.PagePB.Response> getUserAsync(org.apache.dubbo.benchmark.bean.PagePB.Request request) {
    throw new UnsupportedOperationException("No need to override this method, extend XxxImplBase and override all methods it allows.");
    }

    public void getUser(org.apache.dubbo.benchmark.bean.PagePB.Request request, io.grpc.stub.StreamObserver<org.apache.dubbo.benchmark.bean.PagePB.Response> responseObserver);

    default public org.apache.dubbo.benchmark.bean.PagePB.Response listUser(org.apache.dubbo.benchmark.bean.PagePB.Request request) {
    throw new UnsupportedOperationException("No need to override this method, extend XxxImplBase and override all methods it allows.");
    }

    default public com.google.common.util.concurrent.ListenableFuture<org.apache.dubbo.benchmark.bean.PagePB.Response> listUserAsync(org.apache.dubbo.benchmark.bean.PagePB.Request request) {
    throw new UnsupportedOperationException("No need to override this method, extend XxxImplBase and override all methods it allows.");
    }

    public void listUser(org.apache.dubbo.benchmark.bean.PagePB.Request request, io.grpc.stub.StreamObserver<org.apache.dubbo.benchmark.bean.PagePB.Response> responseObserver);

}

public static abstract class UserServiceImplBase implements io.grpc.BindableService, IUserService {

private IUserService proxiedImpl;

public final void setProxiedImpl(IUserService proxiedImpl) {
this.proxiedImpl = proxiedImpl;
}

    @java.lang.Override
    public final org.apache.dubbo.benchmark.bean.PagePB.Response existUser(org.apache.dubbo.benchmark.bean.PagePB.Request request) {
    throw new UnsupportedOperationException("No need to override this method, extend XxxImplBase and override all methods it allows.");
    }

    @java.lang.Override
    public final com.google.common.util.concurrent.ListenableFuture<org.apache.dubbo.benchmark.bean.PagePB.Response> existUserAsync(org.apache.dubbo.benchmark.bean.PagePB.Request request) {
    throw new UnsupportedOperationException("No need to override this method, extend XxxImplBase and override all methods it allows.");
    }

    @java.lang.Override
    public final org.apache.dubbo.benchmark.bean.PagePB.Response createUser(org.apache.dubbo.benchmark.bean.PagePB.Request request) {
    throw new UnsupportedOperationException("No need to override this method, extend XxxImplBase and override all methods it allows.");
    }

    @java.lang.Override
    public final com.google.common.util.concurrent.ListenableFuture<org.apache.dubbo.benchmark.bean.PagePB.Response> createUserAsync(org.apache.dubbo.benchmark.bean.PagePB.Request request) {
    throw new UnsupportedOperationException("No need to override this method, extend XxxImplBase and override all methods it allows.");
    }

    @java.lang.Override
    public final org.apache.dubbo.benchmark.bean.PagePB.Response getUser(org.apache.dubbo.benchmark.bean.PagePB.Request request) {
    throw new UnsupportedOperationException("No need to override this method, extend XxxImplBase and override all methods it allows.");
    }

    @java.lang.Override
    public final com.google.common.util.concurrent.ListenableFuture<org.apache.dubbo.benchmark.bean.PagePB.Response> getUserAsync(org.apache.dubbo.benchmark.bean.PagePB.Request request) {
    throw new UnsupportedOperationException("No need to override this method, extend XxxImplBase and override all methods it allows.");
    }

    @java.lang.Override
    public final org.apache.dubbo.benchmark.bean.PagePB.Response listUser(org.apache.dubbo.benchmark.bean.PagePB.Request request) {
    throw new UnsupportedOperationException("No need to override this method, extend XxxImplBase and override all methods it allows.");
    }

    @java.lang.Override
    public final com.google.common.util.concurrent.ListenableFuture<org.apache.dubbo.benchmark.bean.PagePB.Response> listUserAsync(org.apache.dubbo.benchmark.bean.PagePB.Request request) {
    throw new UnsupportedOperationException("No need to override this method, extend XxxImplBase and override all methods it allows.");
    }

        public void existUser(org.apache.dubbo.benchmark.bean.PagePB.Request request,
        io.grpc.stub.StreamObserver<org.apache.dubbo.benchmark.bean.PagePB.Response> responseObserver) {
        asyncUnimplementedUnaryCall(org.apache.dubbo.benchmark.bean.UserServiceGrpc.getExistUserMethod(), responseObserver);
        }
        public void createUser(org.apache.dubbo.benchmark.bean.PagePB.Request request,
        io.grpc.stub.StreamObserver<org.apache.dubbo.benchmark.bean.PagePB.Response> responseObserver) {
        asyncUnimplementedUnaryCall(org.apache.dubbo.benchmark.bean.UserServiceGrpc.getCreateUserMethod(), responseObserver);
        }
        public void getUser(org.apache.dubbo.benchmark.bean.PagePB.Request request,
        io.grpc.stub.StreamObserver<org.apache.dubbo.benchmark.bean.PagePB.Response> responseObserver) {
        asyncUnimplementedUnaryCall(org.apache.dubbo.benchmark.bean.UserServiceGrpc.getGetUserMethod(), responseObserver);
        }
        public void listUser(org.apache.dubbo.benchmark.bean.PagePB.Request request,
        io.grpc.stub.StreamObserver<org.apache.dubbo.benchmark.bean.PagePB.Response> responseObserver) {
        asyncUnimplementedUnaryCall(org.apache.dubbo.benchmark.bean.UserServiceGrpc.getListUserMethod(), responseObserver);
        }

@java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
    .addMethod(
    org.apache.dubbo.benchmark.bean.UserServiceGrpc.getExistUserMethod(),
    asyncUnaryCall(
    new MethodHandlers<
    org.apache.dubbo.benchmark.bean.PagePB.Request,
    org.apache.dubbo.benchmark.bean.PagePB.Response>(
    proxiedImpl, METHODID_EXIST_USER)))
    .addMethod(
    org.apache.dubbo.benchmark.bean.UserServiceGrpc.getCreateUserMethod(),
    asyncUnaryCall(
    new MethodHandlers<
    org.apache.dubbo.benchmark.bean.PagePB.Request,
    org.apache.dubbo.benchmark.bean.PagePB.Response>(
    proxiedImpl, METHODID_CREATE_USER)))
    .addMethod(
    org.apache.dubbo.benchmark.bean.UserServiceGrpc.getGetUserMethod(),
    asyncUnaryCall(
    new MethodHandlers<
    org.apache.dubbo.benchmark.bean.PagePB.Request,
    org.apache.dubbo.benchmark.bean.PagePB.Response>(
    proxiedImpl, METHODID_GET_USER)))
    .addMethod(
    org.apache.dubbo.benchmark.bean.UserServiceGrpc.getListUserMethod(),
    asyncUnaryCall(
    new MethodHandlers<
    org.apache.dubbo.benchmark.bean.PagePB.Request,
    org.apache.dubbo.benchmark.bean.PagePB.Response>(
    proxiedImpl, METHODID_LIST_USER)))
.build();
}
}
    private static final int METHODID_EXIST_USER = 0;
    private static final int METHODID_CREATE_USER = 1;
    private static final int METHODID_GET_USER = 2;
    private static final int METHODID_LIST_USER = 3;

private static final class MethodHandlers
<Req, Resp> implements
io.grpc.stub.ServerCalls.UnaryMethod
<Req, Resp>,
io.grpc.stub.ServerCalls.ServerStreamingMethod
<Req, Resp>,
io.grpc.stub.ServerCalls.ClientStreamingMethod
<Req, Resp>,
io.grpc.stub.ServerCalls.BidiStreamingMethod
<Req, Resp> {
private final IUserService serviceImpl;
private final int methodId;

MethodHandlers(IUserService serviceImpl, int methodId) {
this.serviceImpl = serviceImpl;
this.methodId = methodId;
}

@java.lang.Override
@java.lang.SuppressWarnings("unchecked")
public void invoke(Req request, io.grpc.stub.StreamObserver
<Resp> responseObserver) {
    switch (methodId) {
            case METHODID_EXIST_USER:
            serviceImpl.existUser((org.apache.dubbo.benchmark.bean.PagePB.Request) request,
            (io.grpc.stub.StreamObserver<org.apache.dubbo.benchmark.bean.PagePB.Response>) responseObserver);
            break;
            case METHODID_CREATE_USER:
            serviceImpl.createUser((org.apache.dubbo.benchmark.bean.PagePB.Request) request,
            (io.grpc.stub.StreamObserver<org.apache.dubbo.benchmark.bean.PagePB.Response>) responseObserver);
            break;
            case METHODID_GET_USER:
            serviceImpl.getUser((org.apache.dubbo.benchmark.bean.PagePB.Request) request,
            (io.grpc.stub.StreamObserver<org.apache.dubbo.benchmark.bean.PagePB.Response>) responseObserver);
            break;
            case METHODID_LIST_USER:
            serviceImpl.listUser((org.apache.dubbo.benchmark.bean.PagePB.Request) request,
            (io.grpc.stub.StreamObserver<org.apache.dubbo.benchmark.bean.PagePB.Response>) responseObserver);
            break;
    default:
    throw new java.lang.AssertionError();
    }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver
    <Req> invoke(io.grpc.stub.StreamObserver
        <Resp> responseObserver) {
            switch (methodId) {
            default:
            throw new java.lang.AssertionError();
            }
            }
            }

            }
