package org.thesong.thesongrpc.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @Author thesong
 * @Date 2020/11/25 20:25
 * @Version 1.0
 * @Describe
 */
public class RpcDecoder<T> extends ByteToMessageDecoder {

    private Class<T> clazz;
    private Serializer serializer;

    public RpcDecoder(Class<T> clazz, Serializer serializer) {
        this.clazz = clazz;
        this.serializer = serializer;
    }


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {
        if(byteBuf.readableBytes()<4){
            return;
        }
        byteBuf.markReaderIndex();
        int dataLength = byteBuf.readInt();
        if(byteBuf.readableBytes()<dataLength){
            byteBuf.resetReaderIndex();
            return;
        }

        byte[] data = new byte[dataLength];
        byteBuf.readBytes(data);
        Object obj = serializer.deserialize(data, clazz);
        list.add(obj);

    }
}
