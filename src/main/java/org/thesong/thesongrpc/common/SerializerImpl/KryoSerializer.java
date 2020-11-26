package org.thesong.thesongrpc.common.SerializerImpl;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.thesong.thesongrpc.common.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @Author thesong
 * @Date 2020/11/26 17:32
 * @Version 1.0
 * @Describe
 */
public class KryoSerializer implements Serializer {

    private static final ThreadLocal<Kryo> kryos = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        //支持循环引用
        kryo.setReferences(true);
        //关闭注册行为
        kryo.setRegistrationRequired(false);
        return kryo;
    });
    @Override
    public  <T> byte[] serialize(T t, Class<T> clazz) {
        Kryo kryo = kryos.get();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);
        kryo.writeClassAndObject(output, t);
        output.close();
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        Kryo kryo = kryos.get();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        Input input = new Input(byteArrayInputStream);
        input.close();
        return (T) kryo.readClassAndObject(input);
    }
}
