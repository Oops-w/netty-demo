package com.wsy.server.handler;

import com.wsy.message.RpcResponseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wsy
 * @date 2021/9/20 5:08 下午
 * @Description
 */
@Slf4j
public class RpcResponseMessageHandler extends SimpleChannelInboundHandler<RpcResponseMessage> {

    public static final Map<Integer, Promise<Object>> promisePool = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponseMessage msg) {
        Promise<Object> promise = promisePool.remove(msg.getSequenceId());
        Exception e = msg.getExceptionValue();
        if (e != null) {
            promise.setFailure(e);
            log.error("rpc response exception", e);
            throw new RuntimeException(e);
        } else {
            promise.setSuccess(msg.getReturnValue());
            log.debug("得到返回结果：{}", msg.getReturnValue());
            System.out.println(msg.getReturnValue());
        }
    }
}
