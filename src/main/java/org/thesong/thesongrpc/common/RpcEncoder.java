package org.thesong.thesongrpc.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @Author thesong
 * @Date 2020/11/25 20:20
 * @Version 1.0
 * @Describe
 */
public class RpcEncoder extends MessageToByteEncoder {

    private Class<?> clazz;
    private Serializer serializer;

    public RpcEncoder(Class<?> clazz, Serializer serializer) {
        this.clazz = clazz;
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object object, ByteBuf byteBuf) throws Exception {
        if(clazz!=null && clazz.isInstance(object)){
            byte[] bytes = serializer.serialize(object);
            byteBuf.writeInt(bytes.length);
            byteBuf.writeBytes(bytes);
        }
    }
}
