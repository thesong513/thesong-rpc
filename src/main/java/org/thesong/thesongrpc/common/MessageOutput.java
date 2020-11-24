package org.thesong.thesongrpc.common;

import com.alibaba.fastjson.JSON;

/**
 * @Author thesong
 * @Date 2020/11/24 14:36
 * @Version 1.0
 * @Describe
 */
public class MessageOutput {
    private String type;
    private String requestId;
    private Object payload;

    public MessageOutput(String type, String requestId, Object payload){
        this.payload=payload;
        this.requestId=requestId;
        this.type=type;
    }

    public String getType(){
        return type;
    }

    public String getRequestId(){
        return requestId;
    }

    public Object getPayload(){
        return payload;
    }


}
