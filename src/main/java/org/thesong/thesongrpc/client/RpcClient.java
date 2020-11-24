package org.thesong.thesongrpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.thesong.thesongrpc.common.*;


import java.util.concurrent.ExecutionException;

import java.util.concurrent.TimeUnit;


/**
 * @Author thesong
 * @Date 2020/11/24 16:10
 * @Version 1.0
 * @Describe
 */
@Slf4j
public class RpcClient {

    private String ip;
    private int port;
    private Bootstrap bootstrap;
    private EventLoopGroup group;
    private MessageCollector collector;
    private boolean started;
    private boolean stopped;

    private MessageRegistry registry = new MessageRegistry();

    public RpcClient(String ip, int port){
        this.ip=ip;
        this.port= port;
        this.init();
    }

    public RpcClient rpc(String type, Class<?> reqClass) {
        registry.register(type, reqClass);
        return this;
    }

    public <T> RpcFuture<T> sendAsync(String type, Object payload) {
        if (!started) {
            connect();
            started = true;
        }
        String requestId = RequestId.next();
        MessageOutput output = new MessageOutput(requestId, type, payload);
        return collector.send(output);
    }

    public <T> T send(String type, Object payload) {
        RpcFuture<T> future = sendAsync(type, payload);
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RpcException(e);
        }
    }

    public void init(){
        bootstrap = new Bootstrap();
        group = new NioEventLoopGroup();
        bootstrap.group(group);
        MessageEncoder encoder = new MessageEncoder();
        collector = new MessageCollector(registry, this);
        bootstrap.channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast(new ReadTimeoutHandler(60));
                        pipeline.addLast(new MessageDecoder());
                        pipeline.addLast(encoder);
                        pipeline.addLast(collector);
                    }
                });
        bootstrap.option(ChannelOption.TCP_NODELAY,true)
                .option(ChannelOption.SO_KEEPALIVE,true);


    }

    public void connect(){
        bootstrap.connect(ip, port).syncUninterruptibly();
    }

    public void reconnect(){
        if(stopped){
            return;
        }
        bootstrap.connect(ip, port).addListener(future -> {
            if(future.isSuccess()){
                return;
            }
            if(!stopped){
                group.schedule(()->{
                    reconnect();
                }, 1, TimeUnit.SECONDS);
            }
            log.error("connect {}:{} failure", ip , port);
        });
    }

    public void close(){
        stopped=true;
        collector.close();
        group.shutdownGracefully(0,5000, TimeUnit.SECONDS);
    }





}
