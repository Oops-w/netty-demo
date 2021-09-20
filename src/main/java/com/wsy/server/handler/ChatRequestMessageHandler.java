package com.wsy.server.handler;

import com.wsy.message.ChatRequestMessage;
import com.wsy.message.ChatResponseMessage;
import com.wsy.server.session.SessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author wsy
 * @date 2021/9/19 7:40 下午
 * @Description
 */
@ChannelHandler.Sharable
public class ChatRequestMessageHandler extends SimpleChannelInboundHandler<ChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ChatRequestMessage chatRequestMessage) throws Exception {
        String to = chatRequestMessage.getTo();
        Channel channel = SessionFactory.getSession().getChannel(to);
        if (channel == null) {
            // send to sender
            channelHandlerContext.channel().writeAndFlush(new ChatResponseMessage(false, "receiver not online or not exist"));
        } else {
            //send to receiver
            channel.writeAndFlush(new ChatResponseMessage(chatRequestMessage.getFrom(), chatRequestMessage.getContent()));
        }
    }
}
