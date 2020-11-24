package org.thesong.thesongrpc.client;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.thesong.thesongrpc.common.MessageInput;
import org.thesong.thesongrpc.common.MessageOutput;
import org.thesong.thesongrpc.common.MessageRegistry;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * @Author thesong
 * @Date 2020/11/24 16:08
 * @Version 1.0
 * @Describe
 */

@Slf4j
@ChannelHandler.Sharable
public class MessageCollector extends ChannelInboundHandlerAdapter {

    private MessageRegistry registry;
    private RpcClient client;
    private ChannelHandlerContext context;
    private ConcurrentMap<String, RpcFuture<?>> pendingTasks = new ConcurrentHashMap<>();

    private Throwable ConnectionClosed = new Exception("rpc connection not active error");

    public MessageCollector(MessageRegistry registry, RpcClient client){
        this.registry = registry;
        this.client = client;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.context = ctx;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception{
        this.context=null;
        pendingTasks.forEach((x ,future)->{
            future.fail(ConnectionClosed);
        });
        pendingTasks.clear();
        ctx.channel().eventLoop().schedule(()->{
            client.reconnect();
        }, 1, TimeUnit.SECONDS);
    }

    public <T> RpcFuture<T> send(MessageOutput output){
        ChannelHandlerContext ctx = context;
        RpcFuture<T> future = new RpcFuture<>();
        if(ctx !=null){
            ctx.channel().eventLoop().execute(()->{
                pendingTasks.put(output.getRequestId(), future);
                ctx.writeAndFlush(output);
            });
        }else {
            future.fail(ConnectionClosed);
        }
        return future;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof MessageInput)) {
            return;
        }
        MessageInput input = (MessageInput) msg;
        // 业务逻辑在这里
        Class<?> clazz = registry.getClazz(input.getType());
        if (clazz == null) {
            log.error("unrecognized msg type {}", input.getType());
            return;
        }
        Object o = input.getPayload(clazz);
        @SuppressWarnings("unchecked")
        RpcFuture<Object> future = (RpcFuture<Object>) pendingTasks.remove(input.getRequestId());
        if (future == null) {
            log.error("future not found with type {}", input.getType());
            return;
        }
        future.success(o);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

    }



    public void close() {
        ChannelHandlerContext ctx = context;
        if (ctx != null) {
            ctx.close();
        }
    }



}
