package org.thesong.thesongrpc.common;

import io.netty.channel.ChannelHandlerContext;

/**
 * @Author thesong
 * @Date 2020/11/24 14:30
 * @Version 1.0
 * @Describe
 */

@FunctionalInterface
public interface IMessageHandler<T> {
    void handle(ChannelHandlerContext ctx, String requestId, T Message);

}
