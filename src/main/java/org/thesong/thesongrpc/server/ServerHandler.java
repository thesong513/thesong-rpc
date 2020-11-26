package org.thesong.thesongrpc.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.thesong.thesongrpc.common.RpcRequest;
import org.thesong.thesongrpc.common.RpcResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author thesong
 * @Date 2020/11/25 21:33
 * @Version 1.0
 * @Describe
 */
@Slf4j
@ChannelHandler.Sharable
public class ServerHandler extends ChannelInboundHandlerAdapter {

    private Map<String, Object> registerMap = new HashMap<>();

    public ServerHandler(Map<String, Object> registerMap) {
        this.registerMap = registerMap;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object object) throws Exception {
        log.info("msg received！");
        RpcResponse rpcResponse = new RpcResponse();
        if(object instanceof RpcRequest) {
            RpcRequest msg = (RpcRequest) object;
            rpcResponse.setRequestId(msg.getRequestId());
            try {
                Object result = handleMessage(msg);
                rpcResponse.setResult(result);
            } catch (Throwable throwable) {
                rpcResponse.setError(throwable.toString());
                throwable.printStackTrace();
            }
        }
        ctx.writeAndFlush(rpcResponse);
    }


    private Object handleMessage(RpcRequest msg) throws Throwable {
        Object result = "no corresponding service or method! ";
        try {
            if (registerMap.containsKey(msg.getClassName())) {
                Object provider = registerMap.get(msg.getClassName());
                result = provider.getClass().getMethod(msg.getMethodName(), msg.getParameterTypes())
                        .invoke(provider, msg.getParameters());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RpcException("rpc call error!");
        }
        return result;
    }


}
