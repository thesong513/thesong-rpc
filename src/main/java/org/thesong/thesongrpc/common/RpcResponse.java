package org.thesong.thesongrpc.common;

import lombok.Data;

/**
 * @Author thesong
 * @Date 2020/11/25 20:09
 * @Version 1.0
 * @Describe
 */

@Data
public class RpcResponse {

    private String requestId;
    private String error;
    private Object result;
}
