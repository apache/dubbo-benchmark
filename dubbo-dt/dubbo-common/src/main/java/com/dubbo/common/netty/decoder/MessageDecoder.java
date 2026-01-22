package com.dubbo.common.netty.decoder;

import com.dubbo.common.entry.Message;
import com.alibaba.fastjson2.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class MessageDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 4) {
            return;
        }
        
        in.markReaderIndex();
        int length = in.readInt();
        
        if (in.readableBytes() < length) {
            in.resetReaderIndex();
            return;
        }
        
        byte[] bytes = new byte[length];
        in.readBytes(bytes);
        String json = new String(bytes, "UTF-8");
        Message baseMsg = JSON.parseObject(json, Message.class);
        out.add(baseMsg);
    }
}