package org.thesong.thesongrpc.client;

import java.lang.reflect.Proxy;

/**
 * @Author thesong
 * @Date 2020/11/25 22:08
 * @Version 1.0
 * @Describe
 */
public class ProxyFactory {
    public static <T> T create(Class<T> interfaceCalss, String host, int port){
        return (T) Proxy.newProxyInstance(interfaceCalss.getClassLoader(),
                new Class<?>[]{interfaceCalss},
                new RpcClientDynamicProxy<T>(interfaceCalss,host,port));
    }
}
