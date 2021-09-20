package com.wsy.server.handler;

import com.wsy.message.GroupCreateRequestMessage;
import com.wsy.message.GroupCreateResponseMessage;
import com.wsy.message.SystemResponseMessage;
import com.wsy.server.session.Group;
import com.wsy.server.session.GroupSessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;
import java.util.Set;

/**
 * @author wsy
 * @date 2021/9/19 8:08 下午
 * @Description
 */
@ChannelHandler.Sharable
public class GroupCreateRequestMessageHandler extends SimpleChannelInboundHandler<GroupCreateRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, GroupCreateRequestMessage groupCreateRequestMessage) throws Exception {
        String groupName = groupCreateRequestMessage.getGroupName();
        Set<String> members = groupCreateRequestMessage.getMembers();
        Group group = GroupSessionFactory.getGroupSession().createGroup(groupName, members);
        if (group == null) {
            // create success
            channelHandlerContext.channel().writeAndFlush(new GroupCreateResponseMessage(true, "create success"));
            List<Channel> channels = GroupSessionFactory.getGroupSession().getMembersChannel(groupName);
            for (Channel channel : channels) {
                // notify member joined group
                channel.writeAndFlush(new SystemResponseMessage(true, "pulled join group name \"" + groupName + "\""));
            }
        } else {
            // create error
            channelHandlerContext.channel().writeAndFlush(new GroupCreateResponseMessage(false, "group name exist"));
        }
    }
}
