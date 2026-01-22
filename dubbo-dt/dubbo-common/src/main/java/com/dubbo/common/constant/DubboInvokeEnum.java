package com.dubbo.common.constant;

import com.alibaba.fastjson2.JSON;


public enum DubboInvokeEnum  {
    AGENT_NAME_HELLO(new DubboTest("你好")),
    AGENT_NAME_HELLO2(new DubboTest("你好号2")),
    AGENT_OTHER(new DubboTest("扩展测试"));
    private Object value;

    DubboInvokeEnum(Object reqObj) {
        this.value = reqObj;
    }
    public Object getValue() {
        return value;
    }
}