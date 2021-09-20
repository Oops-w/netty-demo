package com.wsy.client;

import com.wsy.message.*;
import com.wsy.protocol.MessageCodecSharable;
import com.wsy.protocol.ProtocolFrameDecoder;
import com.wsy.singleton.ThreadPool;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class ChatClient {
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable messageCodecSharable = new MessageCodecSharable();
        ThreadPoolExecutor executor = ThreadPool.getInstance();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        AtomicBoolean loginSuccess = new AtomicBoolean(false);
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(group);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProtocolFrameDecoder());
                    ch.pipeline().addLast(messageCodecSharable);
                    // 3 seconds not send message therefore send "ping" message ensure heartbeat
                    ch.pipeline().addLast(new IdleStateHandler(0, 3, 0));
                    ch.pipeline().addLast(new ChannelDuplexHandler() {
                        @Override
                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                            if (evt instanceof IdleStateEvent) {
                                IdleStateEvent event = (IdleStateEvent) evt;
                                if (event.state() == IdleState.WRITER_IDLE) {
                                    ctx.channel().writeAndFlush(new PingMessage());
                                }
                            }
                        }
                    });
                    // channel active
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                        /**
                         * connected trigger
                         * @param ctx
                         * @throws Exception
                         */
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            startReaderThread(ctx);
                            super.channelActive(ctx);
                        }

                        /**
                         * accept server data judge login result
                         * @param ctx
                         * @param msg
                         * @throws Exception
                         */
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            if (msg instanceof LoginResponseMessage) {
                                LoginResponseMessage response = (LoginResponseMessage) msg;
                                if (response.isSuccess()) {
                                    loginSuccess.set(true);
                                }
                                countDownLatch.countDown();
                            }
                            super.channelRead(ctx, msg);
                        }

                        @Override
                        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                            // TODO unable to close io break thread
                            super.channelInactive(ctx);
                        }

                        /**
                         * open readerThread
                         * @param ctx
                         */
                        private void startReaderThread(ChannelHandlerContext ctx) {
                            executor.execute(() -> {
                                Scanner scanner = new Scanner(System.in);
                                System.out.println("请输入账户:");
                                String username = scanner.nextLine();
                                System.out.println("请输入密码:");
                                String password = scanner.nextLine();
                                LoginRequestMessage message = new LoginRequestMessage(username, password);
                                ctx.writeAndFlush(message);
                                // block waiting check username and password
                                try {
                                    countDownLatch.await();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                if (!loginSuccess.get()) {
                                    ctx.close();
                                    executor.shutdown();
                                    return;
                                }
                                try {
                                    while (true) {
                                        System.out.println("==================================");
                                        // TODO send message does't have space
                                        System.out.println("send [username] [content]");
                                        System.out.println("gsend [group name] [content]");
                                        System.out.println("gcreate [group name] [m1,m2,m3...]");
                                        System.out.println("gmembers [group name]");
                                        System.out.println("gjoin [group name]");
                                        System.out.println("gquit [group name]");
                                        System.out.println("quit");
                                        System.out.println("==================================");
                                        String command = scanner.nextLine();
                                        String[] attr = command.split(" ");
                                        switch (attr[0]) {
                                            case "send":
                                                ctx.writeAndFlush(new ChatRequestMessage(username, attr[1], attr[2]));
                                                break;
                                            case "gsend":
                                                ctx.writeAndFlush(new GroupChatRequestMessage(username, attr[1], attr[2]));
                                                break;
                                            case "gcreate":
                                                // split get members
                                                String[] members = attr[2].split(",");
                                                Set<String> set = new HashSet<>(Arrays.asList(members));
                                                // join group yourself
                                                set.add(username);
                                                ctx.writeAndFlush(new GroupCreateRequestMessage(attr[1], set));
                                                break;
                                            case "gmembers":
                                                ctx.writeAndFlush(new GroupMembersRequestMessage(attr[1]));
                                                break;
                                            case "gjoin":
                                                ctx.writeAndFlush(new GroupJoinRequestMessage(username, attr[1]));
                                                break;
                                            case "gquit":
                                                ctx.writeAndFlush(new GroupQuitRequestMessage(username, attr[1]));
                                                break;
                                            case "quit":
                                                ctx.channel().close();
                                                executor.shutdown();
                                                return;
                                            default:
                                                System.out.println("instructions error please retry");
                                                continue;
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    log.error("{}", e);
                                } finally {
                                    ctx.channel().close();
                                    executor.shutdown();
                                }
                            });
                        }
                    });
                    // deal with "pong"
                    ch.pipeline().addLast(new SimpleChannelInboundHandler<PongMessage>() {
                        @Override
                        protected void channelRead0(ChannelHandlerContext ctx, PongMessage msg) throws Exception {
                        }
                    });
                }
            });
            Channel channel = bootstrap.connect("localhost", 8080).sync().channel();
            channel.closeFuture().sync();
        } catch (Exception e) {
            log.error("client error", e);
        } finally {
            group.shutdownGracefully();
            executor.shutdown();
        }
    }
}
