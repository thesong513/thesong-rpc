package org.thesong.thesongrpc.common.SerializerImpl;

import com.alibaba.fastjson.JSON;
import org.thesong.thesongrpc.common.Serializer;

import java.io.IOException;

/**
 * @Author thesong
 * @Date 2020/11/25 20:17
 * @Version 1.0
 * @Describe
 */
public class JSONSerializer implements Serializer {
    @Override
    public <T> byte[] serialize(T t , Class<T> clazz) throws IOException {
        return JSON.toJSONBytes(t);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException {
        return JSON.parseObject(bytes, clazz);
    }
}
