package org.apache.dubbo.common.api;

import org.apache.dubbo.common.aop.DubboInvokeStat;
import org.apache.dubbo.common.constant.DubboInvokeEnum;

public interface TestService {
    @DubboInvokeStat(namespace = "dubbo-agent",  argKey = "AGENT_NAME_HELLO" ,argValue= DubboInvokeEnum.class)
    String sayHello(String name);
    @DubboInvokeStat(namespace = "dubbo-agent",  argKey = "AGENT_NAME_HELLO",argValue=DubboInvokeEnum.class)
    String sayHello2(String name);
    @DubboInvokeStat(namespace = "agentname", argKey = "AGENT_NAME_HELLO", argValue = DubboInvokeEnum.class)
    String sayHeelow(String name);
    @DubboInvokeStat(namespace = "agentProto", argKey = "AGENT_PROTO", argValue = DubboInvokeEnum.class)
    UserResponse sayHellos(UserRequest request);
}