package org.thesong.thesongrpc.service.impl;

import org.thesong.thesongrpc.service.SomeService;

/**
 * @Author thesong
 * @Date 2020/11/25 12:50
 * @Version 1.0
 * @Describe
 */
public class SomeServiceImpl implements SomeService {
    @Override
    public String hello(String name) {
        return "hello " + name;
    }

    @Override
    public String add(int a, int b) {
        Integer add = a + b;
        return add.toString();
    }
}
