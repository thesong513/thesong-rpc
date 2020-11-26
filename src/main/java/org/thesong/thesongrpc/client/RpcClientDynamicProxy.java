package org.thesong.thesongrpc.client;

import org.thesong.thesongrpc.common.RpcRequest;
import org.thesong.thesongrpc.common.RpcResponse;

import javax.xml.ws.Holder;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.UUID;

/**
 * @Author thesong
 * @Date 2020/11/25 22:00
 * @Version 1.0
 * @Describe
 */
public class RpcClientDynamicProxy<T> implements InvocationHandler {

    private Class<T> clazz;
    private String host;
    private int port;

    public RpcClientDynamicProxy(Class<T> clazz, String host, int port) {
        this.clazz = clazz;
        this.host = host;
        this.port = port;
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
        return response.getResult();
    }
}
