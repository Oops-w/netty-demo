package com.wsy.server.handler;

import com.wsy.message.LoginRequestMessage;
import com.wsy.message.LoginResponseMessage;
import com.wsy.server.service.UserService;
import com.wsy.server.service.UserServiceFactory;
import com.wsy.server.session.SessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author wsy
 * @date 2021/9/19 7:40 下午
 * @Description
 */
@ChannelHandler.Sharable
public class LoginRequestMessageHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, LoginRequestMessage loginRequestMessage) throws Exception {
        UserService userService = UserServiceFactory.getUserService();
        boolean flag = userService.login(loginRequestMessage.getUsername(), loginRequestMessage.getPassword());
        LoginResponseMessage response = new LoginResponseMessage();
        response.setSuccess(flag);
        if (flag) {
            // binding channel
            SessionFactory.getSession().bind(channelHandlerContext.channel(), loginRequestMessage.getUsername());
            response.setReason("login success");
        } else {
            response.setReason("username or password incorrect");
        }
        channelHandlerContext.writeAndFlush(response);
    }
}
