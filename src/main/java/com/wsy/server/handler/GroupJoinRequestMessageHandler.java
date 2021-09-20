package com.wsy.server.handler;

import com.wsy.message.GroupCreateRequestMessage;
import com.wsy.message.GroupJoinRequestMessage;
import com.wsy.message.GroupJoinResponseMessage;
import com.wsy.message.SystemResponseMessage;
import com.wsy.server.session.Group;
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
public class GroupJoinRequestMessageHandler extends SimpleChannelInboundHandler<GroupJoinRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, GroupJoinRequestMessage groupJoinRequestMessage) throws Exception {
        String groupName = groupJoinRequestMessage.getGroupName();
        String username = groupJoinRequestMessage.getUsername();
        Group group = GroupSessionFactory.getGroupSession().joinMember(groupName, username);
        if(group == null) {
            // join fail
            channelHandlerContext.channel().writeAndFlush(new GroupJoinResponseMessage(false, "group not exist"));
        } else {
            // join success
            channelHandlerContext.channel().writeAndFlush(new GroupJoinResponseMessage(true, "join " + groupName + " success"));
            // notify group members
            List<Channel> channels = GroupSessionFactory.getGroupSession().getMembersChannel(groupName);
            for (Channel channel : channels) {
                channel.writeAndFlush(new SystemResponseMessage(true, username + " join group " + groupName));
            }
        }
    }
}
