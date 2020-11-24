package org.thesong.thesongrpc.server;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.thesong.thesongrpc.common.IMessageHandler;
import org.thesong.thesongrpc.common.MessageInput;

/**
 * @Author thesong
 * @Date 2020/11/24 14:35
 * @Version 1.0
 * @Describe
 */
@Slf4j
public class DefaultHandler implements IMessageHandler<MessageInput> {

    @Override
    public void handle(ChannelHandlerContext ctx, String requestId, MessageInput Message) {
        log.error("unrecognized message type {} comes", Message.getType());
        ctx.close();
    }
}
