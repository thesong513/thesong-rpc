package org.thesong.thesongrpc.demo;

import org.thesong.thesongrpc.client.RpcClient;
import org.thesong.thesongrpc.client.RpcException;

/**
 * @Author thesong
 * @Date 2020/11/24 17:00
 * @Version 1.0
 * @Describe
 */
public class DemoClient {
    private RpcClient client;
    public DemoClient(RpcClient client) {
        this.client = client;
        this.client.rpc("fib_res", Long.class)
                .rpc("exp_res", ExpResponse.class);
    }

    public long fib(int n) {
        return (Long) client.send("fib", n);
    }

    public ExpResponse exp(int base, int exp) {
        return (ExpResponse) client.send("exp", new ExpRequest(base, exp));
    }

    public static void main(String[] args) throws InterruptedException {
        RpcClient client = new RpcClient("localhost", 8888);
        DemoClient demo = new DemoClient(client);
        for (int i = 0; i < 30; i++) {
            try {
                System.out.printf("fib(%d) = %d\n", i, demo.fib(i));
                Thread.sleep(100);
            } catch (RpcException e) {
                i--; // retry
            }
        }
        for (int i = 0; i < 30; i++) {
            try {
                ExpResponse res = demo.exp(2, i);
                Thread.sleep(100);
                System.out.printf("exp2(%d) = %d cost=%dns\n", i, res.getValue(), res.getCostInNanos());
            } catch (RpcException e) {
                i--; // retry
            }
        }
        client.close();
    }
}
