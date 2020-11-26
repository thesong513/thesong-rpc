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
public class RpcEncoder<T> extends MessageToByteEncoder {

    private Class<T> clazz;
    private Serializer serializer;

    public RpcEncoder(Class<T> clazz, Serializer serializer) {
        this.clazz = clazz;
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object object, ByteBuf byteBuf) throws Exception {
        if(clazz!=null && clazz.isInstance(object)){
            T t = (T) object;
            byte[] bytes = serializer.serialize(t, clazz);
            byteBuf.writeInt(bytes.length);
            byteBuf.writeBytes(bytes);
        }
    }
}
