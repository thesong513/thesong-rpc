package org.thesong.thesongrpc.client;

/**
 * @Author thesong
 * @Date 2020/11/24 16:18
 * @Version 1.0
 * @Describe
 */
public class RpcException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(String message) {
        super(message);
    }

    public RpcException(Throwable cause) {
        super(cause);
    }
}
