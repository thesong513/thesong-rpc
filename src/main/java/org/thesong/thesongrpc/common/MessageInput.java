package org.thesong.thesongrpc.common;

import com.alibaba.fastjson.JSON;

/**
 * @Author thesong
 * @Date 2020/11/24 14:36
 * @Version 1.0
 * @Describe
 */
public class MessageInput {
    private String type;
    private String requestId;
    private String payload;

    public MessageInput(String type, String requestId, String payload){
        this.payload=payload;
        this.requestId=requestId;
        this.type=type;
    }

    public String getType(){
        return this.type;
    }

    public String getRequestId(){
        return requestId;
    }


    public <T> T getPayload(Class<T> clazz){
        if (payload == null) {
            return null;
        }
        return JSON.parseObject(payload, clazz);
    }


}
