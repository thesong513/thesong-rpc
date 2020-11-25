package org.thesong.thesongrpc.client;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;
import org.thesong.thesongrpc.common.RpcRequest;
import org.thesong.thesongrpc.common.RpcResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @Author thesong
 * @Date 2020/11/25 20:53
 * @Version 1.0
 * @Describe
 */
@Slf4j
@ChannelHandler.Sharable
public class ClientHandler extends ChannelDuplexHandler {

    private final Map<String, DefaultFuture> futureMap = new ConcurrentHashMap<>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof RpcResponse){
            RpcResponse response = (RpcResponse) msg;
            DefaultFuture defaultFuture = futureMap.get(response.getRequestId());
            defaultFuture.setRpcResponse(response);
        }
        super.channelRead(ctx, msg);
    }


    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if(msg instanceof RpcRequest){
            RpcRequest request = (RpcRequest) msg;
            futureMap.putIfAbsent(request.getRequestId(), new DefaultFuture());
        }
        super.write(ctx, msg, promise);
    }

    public RpcResponse getRpcResponse(String requestId){
        try {
            DefaultFuture defaultFuture = futureMap.get(requestId);
            log.info(String.valueOf(futureMap.size()));
            return defaultFuture.getRpcResponse(5000);
        }finally {
            futureMap.remove(requestId);
        }
    }

}
