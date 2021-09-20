package com.wsy.server.handler;

import com.wsy.message.GroupChatRequestMessage;
import com.wsy.message.GroupChatResponseMessage;
import com.wsy.message.GroupMembersRequestMessage;
import com.wsy.message.GroupMembersResponseMessage;
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
public class GroupMembersRequestMessageHandler extends SimpleChannelInboundHandler<GroupMembersRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, GroupMembersRequestMessage groupMembersRequestMessage) throws Exception {
        String groupName = groupMembersRequestMessage.getGroupName();
        channelHandlerContext.channel().writeAndFlush(new GroupMembersResponseMessage(GroupSessionFactory.getGroupSession().getMembers(groupName)));
    }
}
