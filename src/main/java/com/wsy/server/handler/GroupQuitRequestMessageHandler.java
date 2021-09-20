package com.wsy.server.handler;

import com.wsy.message.GroupCreateRequestMessage;
import com.wsy.message.GroupJoinResponseMessage;
import com.wsy.message.GroupQuitRequestMessage;
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
public class GroupQuitRequestMessageHandler extends SimpleChannelInboundHandler<GroupQuitRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, GroupQuitRequestMessage groupQuitRequestMessage) throws Exception {
        String groupName = groupQuitRequestMessage.getGroupName();
        String username = groupQuitRequestMessage.getUsername();
        Group group = GroupSessionFactory.getGroupSession().removeMember(groupName, username);
        if(group == null) {
            // quit fail
            channelHandlerContext.channel().writeAndFlush(new GroupJoinResponseMessage(false, "group not exist or not in this group"));
        } else {
            // quit success
            channelHandlerContext.channel().writeAndFlush(new GroupJoinResponseMessage(true, "quit " + groupName + " success"));
            // notify group members
            List<Channel> channels = GroupSessionFactory.getGroupSession().getMembersChannel(groupName);
            for (Channel channel : channels) {
                channel.writeAndFlush(new SystemResponseMessage(true, username + " quit group " + groupName));
            }
        }
    }
}
