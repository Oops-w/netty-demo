package com.wsy.server.handler;

import com.wsy.message.RpcRequestMessage;
import com.wsy.message.RpcResponseMessage;
import com.wsy.rpc.factory.ServicesFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wsy
 * @date 2021/9/20 5:08 下午
 * @Description
 */
@Slf4j
public class RpcRequestMessageHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage msg) {
        // 返回一个rpc response
        RpcResponseMessage response = new RpcResponseMessage();
        try {
            response.setSequenceId(msg.getSequenceId());
            // 根据传过来的类名 方法名进行执行
            Object obj = ServicesFactory.getInstance(Class.forName(msg.getInterfaceName()));
            // 通过方法名和方法参数类型得到需要执行的方法
            Method method = obj.getClass().getMethod(msg.getMethodName(), msg.getParameterTypes());
            // 执行方法获得结果
            Object result = method.invoke(obj, msg.getParameterValue());
            response.setReturnValue(result);
        } catch (Exception e) {
            // TODO 处理的异常信息 一般都会超过创建的粘包半包处理器的maxsize
            log.error("deal with RpcRequestMessage exception", e);
            String message = e.getCause().getMessage();
            response.setExceptionValue(new RuntimeException(message));
            throw new RuntimeException("deal with RpcRequestMessage exception", e);
        } finally {
            ctx.writeAndFlush(response);
        }
    }
}
