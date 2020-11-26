package org.thesong.thesongrpc.common.SerializerImpl;

import org.thesong.thesongrpc.common.Serializer;

import java.io.IOException;

/**
 * @Author thesong
 * @Date 2020/11/26 15:16
 * @Version 1.0
 * @Describe
 */
public class ProtoBuf implements Serializer {
    @Override
    public byte[] serialize(Object object) throws IOException {
        return new byte[0];
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException {
        return null;
    }
}
