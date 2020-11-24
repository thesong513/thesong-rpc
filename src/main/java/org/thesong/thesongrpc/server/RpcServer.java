package org.thesong.thesongrpc.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.thesong.thesongrpc.common.*;

/**
 * @Author thesong
 * @Date 2020/11/24 13:57
 * @Version 1.0
 * @Describe
 */

@Slf4j
public class RpcServer {

    private String ip;
    private int port;
    private int ioThreads;
    private int workerThreads;
    private MessageHandlers handlers = new MessageHandlers();
    private MessageRegistry registry = new MessageRegistry();

    private ServerBootstrap bootstrap;
    private EventLoopGroup group;
    private MessageCollector collector;
    private Channel serverChannel;

    {
        handlers.defaultHandler(new DefaultHandler());
    }

    public RpcServer(String ip, int port, int ioThreads, int workerThreads){
        this.ip = ip;
        this.port = port;
        this.ioThreads = ioThreads;
        this.workerThreads = workerThreads;
    }


    public RpcServer server(String type, Class<?> reqClass, IMessageHandler<?> handler){
        registry.register(type, reqClass);
        handlers.register(type, handler);
        return this;
    }

    public void start(){
        bootstrap = new ServerBootstrap();
        group = new NioEventLoopGroup(ioThreads);
        bootstrap.group(group);
        collector = new MessageCollector(handlers, registry, workerThreads);

        MessageEncoder encoder = new MessageEncoder();
        bootstrap.channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel channel) throws Exception {
                ChannelPipeline pipeline = channel.pipeline();
                pipeline.addLast(new ReadTimeoutHandler(60));
                // TODO: 2020/11/24 解码器
                pipeline.addLast(new MessageDecoder());
                // TODO: 2020/11/24  编码器
                pipeline.addLast(encoder);
                pipeline.addLast(collector);
            }
        });

        bootstrap.option(ChannelOption.SO_BACKLOG,100)
                .option(ChannelOption.SO_REUSEADDR,true)
                .childOption(ChannelOption.TCP_NODELAY,true)
                .childOption(ChannelOption.SO_KEEPALIVE,true);

        serverChannel = bootstrap.bind(this.ip, this.port).channel();
        log.info("server started @ {}:{}\n", ip, port);
    }

    public void stop(){
        serverChannel.close();
        group.shutdownGracefully();
        collector.closeGracefully();
    }


}
