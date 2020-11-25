package org.thesong.thesongrpc.server;

/**
 * @Author thesong
 * @Date 2020/11/25 13:29
 * @Version 1.0
 * @Describe
 */
public interface Server {

    public void stop() throws Exception;

    public void start() throws Exception;

    void publish(String basePackage) throws Exception;

    public void doRegister() throws Exception;

    public boolean isRunning();

    public int getPort();
}
