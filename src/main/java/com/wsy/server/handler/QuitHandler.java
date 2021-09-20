package com.wsy.server.handler;

import com.wsy.server.session.SessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wsy
 * @date 2021/9/19 10:23 下午
 * @Description
 */
@Slf4j
@ChannelHandler.Sharable
public class QuitHandler extends ChannelInboundHandlerAdapter {
    /**
     * deal with normal break link
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SessionFactory.getSession().unbind(ctx.channel());
        log.debug("{} link disconnected", ctx.channel());
        super.channelInactive(ctx);
    }

    /**
     * deal with exception break link
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        SessionFactory.getSession().unbind(ctx.channel());
        log.debug("{} link disconnected", ctx.channel());
        log.error("exception information ：{}", cause);
        super.exceptionCaught(ctx, cause);
    }
}
