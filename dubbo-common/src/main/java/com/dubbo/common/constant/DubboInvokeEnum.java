package com.dubbo.common.constant;

import com.alibaba.fastjson2.JSON;


public enum DubboInvokeEnum  {
    AGENT_NAME_HELLO(new DubboTest("你好")),
    AGENT_NAME_HELLO2(new DubboTest("你好号2")),
    AGENT_OTHER(new DubboTest("扩展测试"));
    private final DubboTest reqObj;
    private final String jsonValue;

    DubboInvokeEnum(DubboTest reqObj) {
        this.reqObj = reqObj;
        this.jsonValue = JSON.toJSONString(reqObj);
    }

    @Override
    public String toString() {
        return jsonValue;
    }
}