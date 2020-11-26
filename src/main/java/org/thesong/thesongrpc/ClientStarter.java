package org.thesong.thesongrpc;

import lombok.extern.slf4j.Slf4j;
import org.thesong.thesongrpc.client.ProxyFactory;
import org.thesong.thesongrpc.service.SomeService;

import java.lang.reflect.Proxy;

/**
 * @Author thesong
 * @Date 2020/11/25 12:57
 * @Version 1.0
 * @Describe
 */
@Slf4j
public class ClientStarter {
    public static void main(String[] args) {
        final String host = "127.0.0.1";
        final int port = 8080;
        SomeService service = ProxyFactory.create(SomeService.class, host, port);
        log.info(service.hello("lusong"));
    }

}
