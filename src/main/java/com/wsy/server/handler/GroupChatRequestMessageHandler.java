package com.wsy.server.handler;

import com.wsy.message.GroupChatRequestMessage;
import com.wsy.message.GroupChatResponseMessage;
import com.wsy.message.GroupCreateRequestMessage;
import com.wsy.server.session.GroupSession;
import com.wsy.server.session.GroupSessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;

/**
 * @author wsy
 * @date 2021/9/19 8:08 下午
 * @Description
 */
@ChannelHandler.Sharable
public class GroupChatRequestMessageHandler extends SimpleChannelInboundHandler<GroupChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, GroupChatRequestMessage groupChatRequestMessage) throws Exception {
        String groupName = groupChatRequestMessage.getGroupName();
        List<Channel> channels = GroupSessionFactory.getGroupSession().getMembersChannel(groupName);
        for (Channel channel : channels) {
            if(channel == channelHandlerContext.channel()) {
                //TODO special deal with yourself
            }
            channel.writeAndFlush(new GroupChatResponseMessage(groupChatRequestMessage.getFrom(), groupChatRequestMessage.getContent()));
        }
    }
}
