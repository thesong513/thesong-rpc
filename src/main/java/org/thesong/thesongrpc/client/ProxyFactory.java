package org.thesong.thesongrpc.client;

import org.thesong.thesongrpc.common.RpcRequest;
import org.thesong.thesongrpc.common.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * @Author thesong
 * @Date 2020/11/25 22:08
 * @Version 1.0
 * @Describe
 */
public class ProxyFactory<T> implements InvocationHandler {

    private Class<T> clazz;
    private String host;
    private Integer port;

    public ProxyFactory(Class<T> clazz, String host, Integer port){
        this.clazz = clazz;
        this.host = host;
        this.port = port;
    }

    public T create(Class<T> interfaceClass, String host, int port){
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setRequestId(UUID.randomUUID().toString());
        rpcRequest.setClassName(method.getDeclaringClass().getName());
        rpcRequest.setMethodName(method.getName());
        rpcRequest.setParameterTypes(method.getParameterTypes());
        rpcRequest.setParameters(args);
        RpcClient client = new RpcClient(host, port);
        client.connect();
        RpcResponse response = client.send(rpcRequest);
        client.close();
        return response.getResult();
    }

}
