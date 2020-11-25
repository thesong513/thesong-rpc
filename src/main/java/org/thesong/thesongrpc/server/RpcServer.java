package org.thesong.thesongrpc.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.thesong.thesongrpc.common.RpcDecoder;
import org.thesong.thesongrpc.common.RpcEncoder;
import org.thesong.thesongrpc.common.RpcRequest;
import org.thesong.thesongrpc.common.RpcResponse;
import org.thesong.thesongrpc.common.SerializerImpl.JSONSerializer;

import javax.annotation.PreDestroy;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author thesong
 * @Date 2020/11/25 21:18
 * @Version 1.0
 * @Describe
 */
@Slf4j
public class RpcServer {

    private EventLoopGroup ioGroup;
    private EventLoopGroup workerGroup;
    private Channel channel;
    private String host;
    private int port;


    private Map<String, Object> registerMap = new HashMap<>();
    private List<String> classCache = new ArrayList<>();

    public RpcServer(String host, int port){
        this.host = host;
        this.port = port;
    }

    public void publish(String basePackage) throws Exception {
        getProviderClass(basePackage);
        doRegister();
        log.info("registe finished!");
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


    public void start(){
        ioGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(ioGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG,1024)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new RpcEncoder(RpcResponse.class, new JSONSerializer()));
                        pipeline.addLast(new RpcDecoder(RpcRequest.class, new JSONSerializer()));
                        pipeline.addLast();
                    }
                });
        bind(serverBootstrap, host, port);
    }

    public void bind(final ServerBootstrap serverBootstrap ,String host, int port){
        serverBootstrap.bind(host,port).addListener(future -> {
            if(future.isSuccess()){
                log.info("server is running @ {}:{}",host, port);
            }else {
                log.error("port [ {} ] bind failed!",port);
                bind(serverBootstrap,host,port+1);
            }
        });
    }

    @PreDestroy
    public void close() throws InterruptedException {
        ioGroup.shutdownGracefully().sync();
        workerGroup.shutdownGracefully().sync();
        log.info("close server");
    }

}
