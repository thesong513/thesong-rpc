package org.thesong.thesongrpc.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author thesong
 * @Date 2020/11/24 13:57
 * @Version 1.0
 * @Describe
 */

@Slf4j
public class RpcServer implements Server{

    private String ip;
    private int port;
    private int ioThreads;
    private int workerThreads;

    private ServerBootstrap bootstrap;
    private EventLoopGroup group;
    private RpcServerHandler handler;
    private boolean isRuning = false;

    private Map<String, Object> registerMap = new HashMap<>();
    private List<String> classCache = new ArrayList<>();

    public RpcServer(String ip, int port, int ioThreads, int workerThreads){
        this.ip = ip;
        this.port = port;
        this.ioThreads = ioThreads;
        this.workerThreads = workerThreads;
    }

    @Override
    public void publish(String basePackage) throws Exception {
        getProviderClass(basePackage);
        doRegister();
    }

    private void getProviderClass(String basePackage) {
        URL resource = this.getClass().getClassLoader().getResource(basePackage.replace(".", "/"));
        if(null==resource){
            return;
        }
        File file = new File(resource.getFile());
        for (File listFile : file.listFiles()) {
            if(listFile.isDirectory()){
                getProviderClass(basePackage+"."+file.getName());
            }else if(listFile.getName().endsWith(".class")){
                String simpleName = listFile.getName().replace(".class", "");
                classCache.add(basePackage+"."+simpleName);
            }
        }
    }

    @Override
    public void doRegister() throws Exception {
        if(classCache.size()==0){
            return;
        }
        for (String className : classCache) {
            Class<?> clazz = Class.forName(className);
            Class<?>[] interfaces = clazz.getInterfaces();
            if(interfaces.length==1){
                registerMap.put(interfaces[0].getName(), clazz.newInstance());
            }
        }

    }

    @Override
    public boolean isRunning() {
        return isRuning;
    }

    @Override
    public int getPort() {
        return port;
    }


    public void start() throws Exception {
        group = new NioEventLoopGroup(ioThreads);
        bootstrap = new ServerBootstrap();
        bootstrap.group(group);
        handler = new RpcServerHandler(registerMap, workerThreads);
        bootstrap.channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel channel) {
                ChannelPipeline pipeline = channel.pipeline();
                pipeline.addLast(new LengthFieldBasedFrameDecoder(1024, 0, 2, 0, 2));
                pipeline.addLast(new LengthFieldPrepender(2));
                pipeline.addLast(handler);
            }
        });
        bootstrap.option(ChannelOption.SO_BACKLOG,100)
                .option(ChannelOption.SO_REUSEADDR,true)
                .childOption(ChannelOption.TCP_NODELAY,true)
                .childOption(ChannelOption.SO_KEEPALIVE,true);

        ChannelFuture future = bootstrap.bind(this.ip, this.port).sync();
        log.info("server started @ {}:{}", ip, port);
        this.isRuning=true;
        future.channel().closeFuture().sync();
    }

    @Override
    public void stop() throws Exception {
        group.shutdownGracefully().sync();
        log.info("server stoped");
        handler.closeGracefully();
    }

}
