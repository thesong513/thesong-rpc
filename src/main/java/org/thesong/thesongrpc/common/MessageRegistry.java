package org.thesong.thesongrpc.common;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author thesong
 * @Date 2020/11/24 14:48
 * @Version 1.0
 * @Describe
 */
public class MessageRegistry {

    private Map<String, Class<?>> clazzs =new HashMap<>();

    public void register(String type, Class<?> clazz){
        clazzs.put(type, clazz);
    }

    public Class<?> getClazz(String type){
        return clazzs.get(type);
    }

}
