package org.apache.dubbo.common.api;

import org.apache.dubbo.common.aop.DubboInvokeStat;
import org.apache.dubbo.common.constant.DubboInvokeEnum;

public interface GrpcTest extends UserTestService{
    @DubboInvokeStat(namespace = "agentProtoGrpc", argKey = "AGENT_PROTO", argValue = DubboInvokeEnum.class)
    @Override
    UserResponse getUser(UserRequest request);
}
