package org.thesong.thesongrpc.server;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author thesong
 * @Date 2020/11/24 14:44
 * @Version 1.0
 * @Describe
 */
@Slf4j
@ChannelHandler.Sharable
public class RpcServerHandler extends ChannelInboundHandlerAdapter {

    private ThreadPoolExecutor executor;
    private Map<String, Object> registerMap = new HashMap<>();

    public RpcServerHandler(Map<String, Object> registerMap, int workerThreads) {
        ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(1000);
        ThreadFactory threadFactory = new ThreadFactory() {
            AtomicInteger seq = new AtomicInteger();
            //启动线程
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("rpc-" + seq.getAndIncrement());
                log.info(thread.getName()+" is started");
                return thread;
            }
        };
        this.executor = new ThreadPoolExecutor(1, workerThreads, 30, TimeUnit.SECONDS, queue, threadFactory, new ThreadPoolExecutor.CallerRunsPolicy());
        this.registerMap = registerMap;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        this.executor.execute(()->{
            try {
                this.handleMeaasge(ctx, msg);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        });
    }

    public void handleMeaasge(ChannelHandlerContext ctx, Object msg) throws Throwable{
        Object result = "no corresponding service or method! ";
        ByteBuf in= (ByteBuf) msg;
        try {
            String read = in.toString(CharsetUtil.UTF_8);
            Invocation invocation = JSON.parseObject(read, Invocation.class);
            if (registerMap.containsKey(invocation.getClassName())) {
                Object provider = registerMap.get(invocation.getClassName());
                result = provider
                        .getClass()
                        .getMethod(invocation.getMethodName(), invocation.getParamTypes())
                        .invoke(provider, invocation.getParaValues());
            }
        }catch (Exception e){
            throw new RpcException("rpc call error!");
        }
        String resultStr = JSON.toJSONString(result);
        ByteBuf resultIn= Unpooled.buffer();
        resultIn.writeBytes(resultStr.getBytes());
        ctx.writeAndFlush(resultIn).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                ctx.close();
            }
        });
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception{
        //冲刷所有待审消息到远程节点。关闭通道后，操作完成
        log.info("channel Read Complete");

    }

    public void closeGracefully() {
        this.executor.shutdown();
        try {
            this.executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        }
        this.executor.shutdownNow();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("connection comes");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.debug("connection leaves");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("connection", cause);
        ctx.close();
    }

}











