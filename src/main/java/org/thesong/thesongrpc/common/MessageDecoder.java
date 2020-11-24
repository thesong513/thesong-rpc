package org.thesong.thesongrpc.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.ReplayingDecoder;


import java.util.List;

/**
 * @Author thesong
 * @Date 2020/11/24 15:46
 * @Version 1.0
 * @Describe
 */
public class MessageDecoder extends ReplayingDecoder<MessageInput> {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        String requestId = readStr(byteBuf);
        String type = readStr(byteBuf);
        String content = readStr(byteBuf);
        list.add(new MessageInput(type, requestId, content));
    }

    public String readStr(ByteBuf in) {
        int len = in.readInt();
        if (len < 0 || len > (1 << 20)) {
            throw new DecoderException("String too long len=" + len);
        }
        byte[] bytes = new byte[len];
        in.readBytes(bytes);
        return new String(bytes, Charsets.UTF8);
    }
}
