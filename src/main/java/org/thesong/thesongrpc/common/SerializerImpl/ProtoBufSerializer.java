package org.thesong.thesongrpc.common.SerializerImpl;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.runtime.RuntimeSchema;
import org.thesong.thesongrpc.common.Serializer;

import java.io.IOException;

/**
 * @Author thesong
 * @Date 2020/11/26 15:16
 * @Version 1.0
 * @Describe
 */
public class ProtoBufSerializer implements Serializer {


    @Override
    public <T> byte[] serialize(T t, Class<T> clazz) throws IOException {
        return ProtobufIOUtil.toByteArray(t, RuntimeSchema.createFrom(clazz),
                LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
    }

    @Override
    public <T> T deserialize(byte[] data,Class<T> clazz) throws IOException{
        RuntimeSchema<T> runtimeSchema = RuntimeSchema.createFrom(clazz);
        T t = runtimeSchema.newMessage();
        ProtobufIOUtil.mergeFrom(data, t, runtimeSchema);
        return t;
    }

}
