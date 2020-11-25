package org.thesong.thesongrpc.common;

import lombok.Data;

/**
 * @Author thesong
 * @Date 2020/11/25 20:06
 * @Version 1.0
 * @Describe
 */
@Data
public class RpcRequest {

    private String requestId;
    private String className;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;

}
