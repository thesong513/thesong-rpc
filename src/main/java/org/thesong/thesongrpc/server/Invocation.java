package org.thesong.thesongrpc.server;

import lombok.Data;

/**
 * @Author thesong
 * @Date 2020/11/25 10:23
 * @Version 1.0
 * @Describe
 */
@Data
public class Invocation {
    //接口名
    private String className;
    //方法名
    private String methodName;
    //方法参数类型
    private Class<?>[] paramTypes;
    //方法参数数值
    private Object[] paraValues;



}
