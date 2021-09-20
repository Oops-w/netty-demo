package com.wsy.rpc;

import com.wsy.config.Config;
import com.wsy.message.RpcRequestMessage;
import com.wsy.protocol.MessageCodecSharable;
import com.wsy.protocol.ProtocolFrameDecoder;
import com.wsy.rpc.service.HelloService;
import com.wsy.server.handler.RpcRequestMessageHandler;
import com.wsy.server.handler.RpcResponseMessageHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wsy
 * @date 2021/9/20 5:02 下午
 * @Description
 */
@Slf4j
public class wRpcClient {
    private static final AtomicInteger sequenceId = new AtomicInteger(0);
    private static volatile Channel channel = null;
    private static final Object obj = new Object();

    public static void main(String[] args) {
        HelloService helloService = getProxy(HelloService.class);
        helloService.sayHello("zhangsan");
        helloService.sayHello("lisi");
        helloService.sayHello("wangwu");
    }

    /**
     * 根据 serviceClass 创建代理对象
     *
     * @param serviceClass {@link Class} class 对象
     * @param <T>          得到接口的实现类
     * @return
     */
    private static <T> T getProxy(Class<T> serviceClass) {
        Object proxyInstance = Proxy.newProxyInstance(serviceClass.getClassLoader(), new Class[]{serviceClass}, (proxy, method, args) -> {
            RpcRequestMessage message = new RpcRequestMessage(serviceClass.getName(), method.getName(), method.getReturnType(), method.getParameterTypes(), args);
            message.setSequenceId(sequenceId.getAndIncrement());

            // 将rpc 请求发送到server
            getChannel().writeAndFlush(message);
            //创建promise
            DefaultPromise<Object> promise = new DefaultPromise<>(getChannel().eventLoop());
            RpcResponseMessageHandler.promisePool.put(message.getSequenceId(), promise);

            // 阻塞等待 rpc request 处理完后 返回对象
            promise.await();

            if (promise.isSuccess()) {
                return promise.getNow();
            } else {
                Throwable cause = promise.cause();
                log.error("rpc 调用失败", cause);
                throw new RuntimeException(cause);
            }
        });
        return (T) proxyInstance;
    }

    /**
     * 双重检查锁模式 单例模式
     *
     * @return
     */
    private static Channel getChannel() {
        if (channel == null) {
            synchronized (obj) {
                if (channel == null) {
                    init();
                }
            }
        }
        return channel;
    }

    /**
     * 初始化channel
     */
    private static void init() {
        NioEventLoopGroup worker = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap()
                .group(worker)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ProtocolFrameDecoder());
                        ch.pipeline().addLast(new MessageCodecSharable());
                        ch.pipeline().addLast(new RpcResponseMessageHandler());
                    }
                });
        try {
            channel = bootstrap.connect(new InetSocketAddress("localhost", Config.getServerPort())).sync().channel();
            // 当channel 关闭的时候异步关闭 worker线程池
            channel.closeFuture().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    worker.shutdownGracefully();
                }
            });
        } catch (Exception e) {
            log.error("exception", e);
            worker.shutdownGracefully();
            e.printStackTrace();
        }
    }
}
