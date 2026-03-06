package org.apache.dubbo.common.constant;


import io.grpc.stub.StreamObserver;
import org.apache.dubbo.common.api.UserRequest;
import org.apache.dubbo.common.api.UserResponse;

import java.util.Arrays;

public enum

DubboInvokeEnum  {
    AGENT_NAME_HELLO("你好"),
    AGENT_NAME_HELLO2("你好号2"),
    AGENT_OTHER("扩展测试"),
    AGENT_PROTO(UserRequest.newBuilder()
            .setName("你好")
            .build());
    private Object value;

    DubboInvokeEnum(Object reqObj) {
        this.value = reqObj;
    }
    public Object getValue() {
        return value;
    }
}