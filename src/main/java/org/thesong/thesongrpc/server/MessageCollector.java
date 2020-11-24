package org.thesong.thesongrpc.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.thesong.thesongrpc.common.*;

import java.lang.management.PlatformLoggingMXBean;
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
public class MessageCollector extends ChannelInboundHandlerAdapter {

    private ThreadPoolExecutor executor;
    private MessageHandlers handlers;
    private MessageRegistry messageRegistry;

    public MessageCollector(MessageHandlers handlers, MessageRegistry messageRegistry, int workerThreads) {
        ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(1000);
        ThreadFactory threadFactory = new ThreadFactory() {
            AtomicInteger seq = new AtomicInteger();
            //启动线程
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("rpc-" + seq.getAndIncrement());
                return thread;
            }
        };
        this.executor = new ThreadPoolExecutor(1, workerThreads, 30, TimeUnit.SECONDS, queue, threadFactory, new ThreadPoolExecutor.CallerRunsPolicy());
        this.handlers = handlers;
        this.messageRegistry = messageRegistry;
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
    public void channelRead(ChannelHandlerContext ctx, Object message) {
        if (message instanceof MessageInput) {
            this.executor.execute(() -> {
                this.handleMessage(ctx, (MessageInput) message);
            });
        }
    }

    public void handleMessage(ChannelHandlerContext ctx, MessageInput input) {
        Class<?> clazz = messageRegistry.getClazz(input.getType());
        if (clazz == null) {
            handlers.defaultHandler().handle(ctx, input.getRequestId(), input);
            return;
        }

        Object payload = input.getPayload(clazz);

        @SuppressWarnings("unchecked")
        IMessageHandler<Object> handler = (IMessageHandler<Object>) handlers.get(input.getType());
        if (handler != null) {
            handler.handle(ctx, input.getRequestId(), payload);
        } else {
            handlers.defaultHandler().handle(ctx, input.getRequestId(), input);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("connection", cause);
    }

}











