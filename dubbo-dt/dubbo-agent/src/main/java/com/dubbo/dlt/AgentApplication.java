package com.dubbo.dlt;


public class AgentApplication {
    
    public static void main(String[] args) {
        NettyServeragentService nettyServerService = new NettyServeragentService();
        nettyServerService.start();
    }
}