package org.apache.dubbo.agent;


public class AgentApplication {
    
    public static void main(String[] args) {
        NettyServeragentService nettyServerService = new NettyServeragentService();
        nettyServerService.start();
    }
}