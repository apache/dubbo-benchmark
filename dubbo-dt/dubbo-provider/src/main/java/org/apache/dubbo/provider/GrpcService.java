package org.apache.dubbo.provider;

import io.grpc.stub.StreamObserver;
import org.apache.dubbo.common.api.*;

import org.apache.dubbo.config.annotation.DubboService;
@DubboService
public class GrpcService  implements UserTestService {

    @Override
    public UserResponse getUser(UserRequest request) {
        return UserResponse.newBuilder().setGreeting("你好").build();
    }

}
