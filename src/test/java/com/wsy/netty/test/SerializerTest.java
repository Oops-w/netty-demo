package com.wsy.netty.test;

import com.wsy.config.Config;
import com.wsy.message.PingMessage;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.junit.Test;

/**
 * @author wsy
 * @date 2021/9/20 9:47 上午
 * @Description
 */
public class SerializerTest {
    @Test
    public void testSerializer() {
        byte[] bytes = Config.getSerializerAlgorithm().serialize(new PingMessage());
        System.out.println(Config.getSerializerAlgorithm().deserialize(PingMessage.class, bytes));
    }

    @Test
    public void testBacklog() throws InterruptedException {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.option(ChannelOption.SO_BACKLOG, 2);
        bootstrap.group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {

                    }
                })
                .bind(8080)
                .channel()
                .closeFuture().sync();
    }
}
