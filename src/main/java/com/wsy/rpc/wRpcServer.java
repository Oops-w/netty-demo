package com.wsy.rpc;

import com.wsy.config.Config;
import com.wsy.protocol.MessageCodecSharable;
import com.wsy.protocol.ProtocolFrameDecoder;
import com.wsy.server.handler.RpcRequestMessageHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wsy
 * @date 2021/9/20 4:37 下午
 * @Description
 */
@Slf4j
public class wRpcServer {
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup boss = new NioEventLoopGroup(1);
        NioEventLoopGroup worker = new NioEventLoopGroup();

        try {
            ChannelFuture future = new ServerBootstrap()
                    .group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            // 粘包半包处理器
                            ch.pipeline().addLast(new ProtocolFrameDecoder());
                            // 日志处理器
                            ch.pipeline().addLast(new LoggingHandler());
                            // 协议处理器
                            ch.pipeline().addLast(new MessageCodecSharable());
                            // 处理rpc request
                            ch.pipeline().addLast(new RpcRequestMessageHandler());
                        }
                    })
                    .bind(Config.getServerPort());
            Channel channel = future.channel();
            // 阻塞等待连接成功
            future.sync();
            channel.closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("wRpc error", e);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

}
