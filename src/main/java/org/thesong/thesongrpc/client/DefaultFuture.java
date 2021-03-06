package org.thesong.thesongrpc.client;

import org.thesong.thesongrpc.common.RpcResponse;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @Author thesong
 * @Date 2020/11/25 20:54
 * @Version 1.0
 * @Describe
 */
public class DefaultFuture {


    private RpcResponse rpcResponse;
    private volatile boolean isSuccess = false;
    // 锁对象
    private final Object object = new Object();

    public RpcResponse getRpcResponse(int timeout){
        synchronized (object){
            while (!isSuccess){
                try {
                    object.wait(timeout);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            return rpcResponse;
        }
    }

    public void setRpcResponse(RpcResponse rpcResponse){
        if(isSuccess){
            return;
        }
        synchronized (object){
            this.rpcResponse = rpcResponse;
            this.isSuccess = true;
            object.notify();
        }
    }
}
