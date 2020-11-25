package org.thesong.thesongrpc.server;

/**
 * @Author thesong
 * @Date 2020/11/25 10:48
 * @Version 1.0
 * @Describe
 */
public class RpcException extends Exception{

    private static final long serialVersionUID = 3455708526465670030L;

    public RpcException(String msg){
        super(msg);
    }

    public RpcException(String msg,String code){
        super(msg+":--:"+code);
    }
}
