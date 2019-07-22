package org.apache.dubbo.benchmark.bean;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.19.0)",
    comments = "Source: Page.proto")
public final class UserServiceGrpc {

  private UserServiceGrpc() {}

  public static final String SERVICE_NAME = "org.apache.dubbo.benchmark.bean.UserService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<org.apache.dubbo.benchmark.bean.PagePB.Request,
      org.apache.dubbo.benchmark.bean.PagePB.Response> getExistUserMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "existUser",
      requestType = org.apache.dubbo.benchmark.bean.PagePB.Request.class,
      responseType = org.apache.dubbo.benchmark.bean.PagePB.Response.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.apache.dubbo.benchmark.bean.PagePB.Request,
      org.apache.dubbo.benchmark.bean.PagePB.Response> getExistUserMethod() {
    io.grpc.MethodDescriptor<org.apache.dubbo.benchmark.bean.PagePB.Request, org.apache.dubbo.benchmark.bean.PagePB.Response> getExistUserMethod;
    if ((getExistUserMethod = UserServiceGrpc.getExistUserMethod) == null) {
      synchronized (UserServiceGrpc.class) {
        if ((getExistUserMethod = UserServiceGrpc.getExistUserMethod) == null) {
          UserServiceGrpc.getExistUserMethod = getExistUserMethod = 
              io.grpc.MethodDescriptor.<org.apache.dubbo.benchmark.bean.PagePB.Request, org.apache.dubbo.benchmark.bean.PagePB.Response>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "org.apache.dubbo.benchmark.bean.UserService", "existUser"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.apache.dubbo.benchmark.bean.PagePB.Request.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.apache.dubbo.benchmark.bean.PagePB.Response.getDefaultInstance()))
                  .setSchemaDescriptor(new UserServiceMethodDescriptorSupplier("existUser"))
                  .build();
          }
        }
     }
     return getExistUserMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.apache.dubbo.benchmark.bean.PagePB.Request,
      org.apache.dubbo.benchmark.bean.PagePB.Response> getCreateUserMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "createUser",
      requestType = org.apache.dubbo.benchmark.bean.PagePB.Request.class,
      responseType = org.apache.dubbo.benchmark.bean.PagePB.Response.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.apache.dubbo.benchmark.bean.PagePB.Request,
      org.apache.dubbo.benchmark.bean.PagePB.Response> getCreateUserMethod() {
    io.grpc.MethodDescriptor<org.apache.dubbo.benchmark.bean.PagePB.Request, org.apache.dubbo.benchmark.bean.PagePB.Response> getCreateUserMethod;
    if ((getCreateUserMethod = UserServiceGrpc.getCreateUserMethod) == null) {
      synchronized (UserServiceGrpc.class) {
        if ((getCreateUserMethod = UserServiceGrpc.getCreateUserMethod) == null) {
          UserServiceGrpc.getCreateUserMethod = getCreateUserMethod = 
              io.grpc.MethodDescriptor.<org.apache.dubbo.benchmark.bean.PagePB.Request, org.apache.dubbo.benchmark.bean.PagePB.Response>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "org.apache.dubbo.benchmark.bean.UserService", "createUser"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.apache.dubbo.benchmark.bean.PagePB.Request.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.apache.dubbo.benchmark.bean.PagePB.Response.getDefaultInstance()))
                  .setSchemaDescriptor(new UserServiceMethodDescriptorSupplier("createUser"))
                  .build();
          }
        }
     }
     return getCreateUserMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.apache.dubbo.benchmark.bean.PagePB.Request,
      org.apache.dubbo.benchmark.bean.PagePB.Response> getGetUserMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "getUser",
      requestType = org.apache.dubbo.benchmark.bean.PagePB.Request.class,
      responseType = org.apache.dubbo.benchmark.bean.PagePB.Response.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.apache.dubbo.benchmark.bean.PagePB.Request,
      org.apache.dubbo.benchmark.bean.PagePB.Response> getGetUserMethod() {
    io.grpc.MethodDescriptor<org.apache.dubbo.benchmark.bean.PagePB.Request, org.apache.dubbo.benchmark.bean.PagePB.Response> getGetUserMethod;
    if ((getGetUserMethod = UserServiceGrpc.getGetUserMethod) == null) {
      synchronized (UserServiceGrpc.class) {
        if ((getGetUserMethod = UserServiceGrpc.getGetUserMethod) == null) {
          UserServiceGrpc.getGetUserMethod = getGetUserMethod = 
              io.grpc.MethodDescriptor.<org.apache.dubbo.benchmark.bean.PagePB.Request, org.apache.dubbo.benchmark.bean.PagePB.Response>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "org.apache.dubbo.benchmark.bean.UserService", "getUser"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.apache.dubbo.benchmark.bean.PagePB.Request.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.apache.dubbo.benchmark.bean.PagePB.Response.getDefaultInstance()))
                  .setSchemaDescriptor(new UserServiceMethodDescriptorSupplier("getUser"))
                  .build();
          }
        }
     }
     return getGetUserMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.apache.dubbo.benchmark.bean.PagePB.Request,
      org.apache.dubbo.benchmark.bean.PagePB.Response> getListUserMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "listUser",
      requestType = org.apache.dubbo.benchmark.bean.PagePB.Request.class,
      responseType = org.apache.dubbo.benchmark.bean.PagePB.Response.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.apache.dubbo.benchmark.bean.PagePB.Request,
      org.apache.dubbo.benchmark.bean.PagePB.Response> getListUserMethod() {
    io.grpc.MethodDescriptor<org.apache.dubbo.benchmark.bean.PagePB.Request, org.apache.dubbo.benchmark.bean.PagePB.Response> getListUserMethod;
    if ((getListUserMethod = UserServiceGrpc.getListUserMethod) == null) {
      synchronized (UserServiceGrpc.class) {
        if ((getListUserMethod = UserServiceGrpc.getListUserMethod) == null) {
          UserServiceGrpc.getListUserMethod = getListUserMethod = 
              io.grpc.MethodDescriptor.<org.apache.dubbo.benchmark.bean.PagePB.Request, org.apache.dubbo.benchmark.bean.PagePB.Response>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "org.apache.dubbo.benchmark.bean.UserService", "listUser"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.apache.dubbo.benchmark.bean.PagePB.Request.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.apache.dubbo.benchmark.bean.PagePB.Response.getDefaultInstance()))
                  .setSchemaDescriptor(new UserServiceMethodDescriptorSupplier("listUser"))
                  .build();
          }
        }
     }
     return getListUserMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static UserServiceStub newStub(io.grpc.Channel channel) {
    return new UserServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static UserServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new UserServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static UserServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new UserServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class UserServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void existUser(org.apache.dubbo.benchmark.bean.PagePB.Request request,
        io.grpc.stub.StreamObserver<org.apache.dubbo.benchmark.bean.PagePB.Response> responseObserver) {
      asyncUnimplementedUnaryCall(getExistUserMethod(), responseObserver);
    }

    /**
     */
    public void createUser(org.apache.dubbo.benchmark.bean.PagePB.Request request,
        io.grpc.stub.StreamObserver<org.apache.dubbo.benchmark.bean.PagePB.Response> responseObserver) {
      asyncUnimplementedUnaryCall(getCreateUserMethod(), responseObserver);
    }

    /**
     */
    public void getUser(org.apache.dubbo.benchmark.bean.PagePB.Request request,
        io.grpc.stub.StreamObserver<org.apache.dubbo.benchmark.bean.PagePB.Response> responseObserver) {
      asyncUnimplementedUnaryCall(getGetUserMethod(), responseObserver);
    }

    /**
     */
    public void listUser(org.apache.dubbo.benchmark.bean.PagePB.Request request,
        io.grpc.stub.StreamObserver<org.apache.dubbo.benchmark.bean.PagePB.Response> responseObserver) {
      asyncUnimplementedUnaryCall(getListUserMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getExistUserMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.apache.dubbo.benchmark.bean.PagePB.Request,
                org.apache.dubbo.benchmark.bean.PagePB.Response>(
                  this, METHODID_EXIST_USER)))
          .addMethod(
            getCreateUserMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.apache.dubbo.benchmark.bean.PagePB.Request,
                org.apache.dubbo.benchmark.bean.PagePB.Response>(
                  this, METHODID_CREATE_USER)))
          .addMethod(
            getGetUserMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.apache.dubbo.benchmark.bean.PagePB.Request,
                org.apache.dubbo.benchmark.bean.PagePB.Response>(
                  this, METHODID_GET_USER)))
          .addMethod(
            getListUserMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.apache.dubbo.benchmark.bean.PagePB.Request,
                org.apache.dubbo.benchmark.bean.PagePB.Response>(
                  this, METHODID_LIST_USER)))
          .build();
    }
  }

  /**
   */
  public static final class UserServiceStub extends io.grpc.stub.AbstractStub<UserServiceStub> {
    private UserServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private UserServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected UserServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new UserServiceStub(channel, callOptions);
    }

    /**
     */
    public void existUser(org.apache.dubbo.benchmark.bean.PagePB.Request request,
        io.grpc.stub.StreamObserver<org.apache.dubbo.benchmark.bean.PagePB.Response> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getExistUserMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void createUser(org.apache.dubbo.benchmark.bean.PagePB.Request request,
        io.grpc.stub.StreamObserver<org.apache.dubbo.benchmark.bean.PagePB.Response> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getCreateUserMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getUser(org.apache.dubbo.benchmark.bean.PagePB.Request request,
        io.grpc.stub.StreamObserver<org.apache.dubbo.benchmark.bean.PagePB.Response> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetUserMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void listUser(org.apache.dubbo.benchmark.bean.PagePB.Request request,
        io.grpc.stub.StreamObserver<org.apache.dubbo.benchmark.bean.PagePB.Response> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getListUserMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class UserServiceBlockingStub extends io.grpc.stub.AbstractStub<UserServiceBlockingStub> {
    private UserServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private UserServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected UserServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new UserServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public org.apache.dubbo.benchmark.bean.PagePB.Response existUser(org.apache.dubbo.benchmark.bean.PagePB.Request request) {
      return blockingUnaryCall(
          getChannel(), getExistUserMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.apache.dubbo.benchmark.bean.PagePB.Response createUser(org.apache.dubbo.benchmark.bean.PagePB.Request request) {
      return blockingUnaryCall(
          getChannel(), getCreateUserMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.apache.dubbo.benchmark.bean.PagePB.Response getUser(org.apache.dubbo.benchmark.bean.PagePB.Request request) {
      return blockingUnaryCall(
          getChannel(), getGetUserMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.apache.dubbo.benchmark.bean.PagePB.Response listUser(org.apache.dubbo.benchmark.bean.PagePB.Request request) {
      return blockingUnaryCall(
          getChannel(), getListUserMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class UserServiceFutureStub extends io.grpc.stub.AbstractStub<UserServiceFutureStub> {
    private UserServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private UserServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected UserServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new UserServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.apache.dubbo.benchmark.bean.PagePB.Response> existUser(
        org.apache.dubbo.benchmark.bean.PagePB.Request request) {
      return futureUnaryCall(
          getChannel().newCall(getExistUserMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.apache.dubbo.benchmark.bean.PagePB.Response> createUser(
        org.apache.dubbo.benchmark.bean.PagePB.Request request) {
      return futureUnaryCall(
          getChannel().newCall(getCreateUserMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.apache.dubbo.benchmark.bean.PagePB.Response> getUser(
        org.apache.dubbo.benchmark.bean.PagePB.Request request) {
      return futureUnaryCall(
          getChannel().newCall(getGetUserMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.apache.dubbo.benchmark.bean.PagePB.Response> listUser(
        org.apache.dubbo.benchmark.bean.PagePB.Request request) {
      return futureUnaryCall(
          getChannel().newCall(getListUserMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_EXIST_USER = 0;
  private static final int METHODID_CREATE_USER = 1;
  private static final int METHODID_GET_USER = 2;
  private static final int METHODID_LIST_USER = 3;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final UserServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(UserServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
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
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class UserServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    UserServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return org.apache.dubbo.benchmark.bean.PagePB.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("UserService");
    }
  }

  private static final class UserServiceFileDescriptorSupplier
      extends UserServiceBaseDescriptorSupplier {
    UserServiceFileDescriptorSupplier() {}
  }

  private static final class UserServiceMethodDescriptorSupplier
      extends UserServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    UserServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (UserServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new UserServiceFileDescriptorSupplier())
              .addMethod(getExistUserMethod())
              .addMethod(getCreateUserMethod())
              .addMethod(getGetUserMethod())
              .addMethod(getListUserMethod())
              .build();
        }
      }
    }
    return result;
  }
}
