package org.thesong.thesongrpc.common;

import java.io.IOException;

/**
 * @Author thesong
 * @Date 2020/11/25 20:13
 * @Version 1.0
 * @Describe
 */
public interface Serializer {

    //序列化 java转二进制
    <T> byte[] serialize(T t, Class<T> clazz) throws IOException;

    //二进制转java
    <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException;

}
