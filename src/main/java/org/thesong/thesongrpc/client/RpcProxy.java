package org.thesong.thesongrpc.client;

import com.alibaba.fastjson.JSON;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.util.CharsetUtil;
import org.thesong.thesongrpc.server.Invocation;

import javax.sound.sampled.Port;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.charset.Charset;

/**
 * @Author thesong
 * @Date 2020/11/25 12:56
 * @Version 1.0
 * @Describe
 */


public class RpcProxy {

    public static <T> T getRemoteProxyObj(Class<?> clazz, String host, int port) {
        // 将本地的接口调用转换成JDK的动态代理，在动态代理中实现接口的远程调用
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),
                new Class[]{clazz},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        if (Object.class.equals(method.getDeclaredAnnotations())) {
                            return method.invoke(this, args);
                        }
                        return rpcInvoke(clazz, method, args, host, port);
                    }
                });
    }

    private static Object rpcInvoke(Class<?> clazz, Method method, Object[] args, String host, int port) throws Exception {
        RpcClientHandler rpcClientHandler = new RpcClientHandler();
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            Invocation invocation = new Invocation();
                            invocation.setClassName(clazz.getName());
                            invocation.setMethodName(method.getName());
                            invocation.setParamTypes(method.getParameterTypes());
                            invocation.setParaValues(args);
                            rpcClientHandler.setInvocation(invocation);
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(1024, 0, 2, 0, 2));
                            pipeline.addLast(new LengthFieldPrepender(2));
                            pipeline.addLast(rpcClientHandler);
                        }
                    });
            ChannelFuture future = bootstrap.connect(host, port).sync();
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
        return rpcClientHandler.getResult();
    }
}
