package org.thesong.thesongrpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;
import org.thesong.thesongrpc.common.RpcDecoder;
import org.thesong.thesongrpc.common.RpcEncoder;
import org.thesong.thesongrpc.common.RpcRequest;
import org.thesong.thesongrpc.common.RpcResponse;
import org.thesong.thesongrpc.common.SerializerImpl.JSONSerializer;
import org.thesong.thesongrpc.common.SerializerImpl.KryoSerializer;
import org.thesong.thesongrpc.common.SerializerImpl.ProtoBufSerializer;

import javax.annotation.PreDestroy;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @Author thesong
 * @Date 2020/11/25 12:55
 * @Version 1.0
 * @Describe
 */
@Slf4j
public class RpcClient {
    private EventLoopGroup eventLoopGroup;
    private Channel channel;
    private String host;
    private int port;
    private ClientHandler clientHandler;
    private static final int MAX_RETRY = 5;

    public RpcClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect() throws Exception {
        clientHandler = new ClientHandler();
        eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new LengthFieldBasedFrameDecoder(65535, 0, 4));
                        pipeline.addLast(new RpcDecoder(RpcResponse.class, new KryoSerializer()));
                        pipeline.addLast(new RpcEncoder(RpcRequest.class, new KryoSerializer()));
                        pipeline.addLast(clientHandler);
                    }
                });
        connect(bootstrap, host, port, MAX_RETRY);
    }

    private void connect(Bootstrap bootstrap, String host, int port, int retry) throws  Exception{
        ChannelFuture channelFuture = bootstrap.connect(host, port).sync().addListener(future -> {
            if (future.isSuccess()) {} else if (retry == 0) {
                log.error("已经达到最大连接次数！ ");
            } else {
                int order = MAX_RETRY - retry + 1;
                int delay = 1 << order;
                log.error("{} : 连接失败，还会尝试{}次连接 ...", new Date(), order);
                bootstrap.config().group().schedule(() -> {
                    try {
                        connect(bootstrap, host, port, retry - 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, delay, TimeUnit.SECONDS);
            }
        });
        channel = channelFuture.channel();
    }

    public RpcResponse send(final RpcRequest rpcRequest) throws Exception{
        try {
            channel.writeAndFlush(rpcRequest).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return clientHandler.getRpcResponse(rpcRequest.getRequestId());
    }

    @PreDestroy
    public void close() throws InterruptedException {
        eventLoopGroup.shutdownGracefully();
        channel.closeFuture().sync();
    }

}
