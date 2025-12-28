package com.dubbo.common.api;

import com.dubbo.common.aop.DubboInvokeStat;
import com.dubbo.common.constant.DubboInvokeEnum;

public interface TestService {
    @DubboInvokeStat(value = "agentname",  argValue= DubboInvokeEnum.AGENT_NAME_HELLO)
    String sayHello(String name);
    @DubboInvokeStat(value = "agentname",  argValue=DubboInvokeEnum.AGENT_NAME_HELLO2)
    String sayHello2(String name);
    @DubboInvokeStat(value = "agent", argValue = DubboInvokeEnum.AGENT_OTHER)
    String sayHeelow(String name);
}