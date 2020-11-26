package org.thesong.thesongrpc;

import lombok.extern.slf4j.Slf4j;
import org.thesong.thesongrpc.server.RpcServer;

/**
 * @Author thesong
 * @Date 2020/11/25 12:43
 * @Version 1.0
 * @Describe
 */
@Slf4j
public class ServerStarter {


    public static void main(String[] args) throws Exception {
        final String host = "127.0.0.1";
        final int port = 8080;
        RpcServer server = new RpcServer(host, port);
        server.publish("org.thesong.thesongrpc.service.impl");
        server.start();
    }

}
