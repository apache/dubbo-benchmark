package com.dubbo.common.netty.encoder;

import com.dubbo.common.entry.Message;
import com.alibaba.fastjson2.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MessageEncoder extends MessageToByteEncoder<Message> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        String json = JSON.toJSONString(msg);
        byte[] bytes = json.getBytes("UTF-8");
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }
}