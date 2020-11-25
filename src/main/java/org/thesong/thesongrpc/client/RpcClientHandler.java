package org.thesong.thesongrpc.client;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.thesong.thesongrpc.server.Invocation;

/**
 * @Author thesong
 * @Date 2020/11/25 12:56
 * @Version 1.0
 * @Describe
 */
@Slf4j
@Data
@ChannelHandler.Sharable
public class RpcClientHandler extends ChannelInboundHandlerAdapter {

    private Invocation invocation;
    private Object result;


    public Object getResult() {
        return result;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws InterruptedException {
        log.info("connection is established!");
        ctx.writeAndFlush(Unpooled.copiedBuffer(JSON.toJSONString(invocation), CharsetUtil.UTF_8));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        String s = in.toString(CharsetUtil.UTF_8);
        this.result = (Object) s;
        ctx.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
