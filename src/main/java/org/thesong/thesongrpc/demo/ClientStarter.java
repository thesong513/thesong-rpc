package org.thesong.thesongrpc.demo;

import lombok.extern.slf4j.Slf4j;
import org.thesong.thesongrpc.client.RpcProxy;
import org.thesong.thesongrpc.service.SomeService;

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
        final int port = 8088;
        SomeService service = RpcProxy.getRemoteProxyObj(SomeService.class, host, port);
        String add = service.hello("thesong");
        log.info(add);
    }

}
