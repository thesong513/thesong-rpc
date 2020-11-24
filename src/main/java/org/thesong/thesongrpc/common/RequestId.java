package org.thesong.thesongrpc.common;

import java.util.UUID;

/**
 * @Author thesong
 * @Date 2020/11/24 14:34
 * @Version 1.0
 * @Describe
 */
public class RequestId {

    public static String next(){
        return UUID.randomUUID().toString();
    }
}
