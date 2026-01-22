package com.dubbo.common.api;

import com.dubbo.common.aop.DubboInvokeStat;
import com.dubbo.common.constant.DubboInvokeEnum;

public interface TestService {
    @DubboInvokeStat(namespace = "dubbo-agent",  argKey = "AGENT_NAME_HELLO" ,argValue= DubboInvokeEnum.class)
    String sayHello(String name);
    @DubboInvokeStat(namespace = "dubbo-agent",  argKey = "AGENT_NAME_HELLO",argValue=DubboInvokeEnum.class)
    String sayHello2(String name);
    @DubboInvokeStat(namespace = "agentname", argKey = "AGENT_NAME_HELLO", argValue = DubboInvokeEnum.class)
    String sayHeelow(String name);
}