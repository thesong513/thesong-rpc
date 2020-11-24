package org.thesong.thesongrpc.common;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;


/**
 * @Author thesong
 * @Date 2020/11/24 15:46
 * @Version 1.0
 * @Describe
 */

@ChannelHandler.Sharable
public class MessageEncoder extends MessageToMessageEncoder<MessageOutput> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, MessageOutput message, List list) throws Exception {
        ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer();
        writeStr(byteBuf, message.getRequestId());
        writeStr(byteBuf, message.getType());
        writeStr(byteBuf, JSONObject.toJSONString(message.getPayload()));
        list.add(byteBuf);

    }

    public void writeStr(ByteBuf buf, String s){
        buf.writeInt(s.length());
        buf.writeBytes(s.getBytes(Charsets.UTF8));
    }

}
