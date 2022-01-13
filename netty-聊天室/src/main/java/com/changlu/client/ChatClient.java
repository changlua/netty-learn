package com.changlu.client;

import com.changlu.message.*;
import com.changlu.protocol.MessageCodecSharable;
import com.changlu.protocol.ProcotolFrameDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class ChatClient {
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        //计数器
        CountDownLatch WAIT_FOR_LOGIN = new CountDownLatch(1);
        AtomicBoolean LOGIN = new AtomicBoolean(false);
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(group);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProcotolFrameDecoder());
//                    ch.pipeline().addLast(LOGGING_HANDLER);
                    ch.pipeline().addLast(MESSAGE_CODEC);
                    ch.pipeline().addLast("client handler", new ChannelInboundHandlerAdapter(){

                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            log.debug("msg: {}", msg);
                            if (msg instanceof LoginResponseMessage) {
                                LoginResponseMessage response = (LoginResponseMessage) msg;
                                if (response.isSuccess()){
                                    LOGIN.set(true);
                                }
                                WAIT_FOR_LOGIN.countDown();
                            }
                        }

                        @Override
                        public void channelActive(ChannelHandlerContext ctx){
                            //负责接收用户在控制台上的输入，负责向服务器发送数据
                            new Thread(()->{
                                Scanner scanner = new Scanner(System.in);
                                System.out.println("请输入用户名：");
                                String username = scanner.nextLine();
                                System.out.println("请输入密码：");
                                String password = scanner.nextLine();
                                //构造登陆消息对象
                                Message message = new LoginRequestMessage(username, password);
                                ctx.channel().writeAndFlush(message);
                                try {
                                    WAIT_FOR_LOGIN.await();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                if (!LOGIN.get()) {
                                    System.out.println("登陆失败！");
                                    ctx.channel().close();
                                    return;
                                }
                                System.out.println("登陆成功！");
                                while (true) {
                                    System.out.println("==================================");
                                    System.out.println("send [username] [content]");
                                    System.out.println("gsend [group name] [content]");
                                    System.out.println("gcreate [group name] [m1,m2,m3...]");
                                    System.out.println("gmembers [group name]");
                                    System.out.println("gjoin [group name]");
                                    System.out.println("gquit [group name]");
                                    System.out.println("quit");
                                    System.out.println("==================================");
                                    String command = scanner.nextLine();
                                    String[] split = command.split(" ");
                                    switch (split[0]){
                                        case "send" :
                                            ctx.writeAndFlush(new ChatRequestMessage(username, split[1], split[2]));
                                            break;
                                        case "gsend" :
                                            ctx.writeAndFlush(new GroupChatRequestMessage(username, split[1], split[2]));
                                            break;
                                        case "gcreate" :
                                            Set<String> users = new HashSet<>(Arrays.asList(split[2].split(",")));
                                            ctx.writeAndFlush(new GroupCreateRequestMessage(split[0],users));
                                            break;
                                        case "gmembers" :
                                            ctx.writeAndFlush(new GroupMembersRequestMessage(split[1]));
                                            break;
                                        case "gjoin" :
                                            ctx.writeAndFlush(new GroupJoinRequestMessage(username, split[1]));
                                            break;
                                        case "gquit" :
                                            ctx.writeAndFlush(new GroupQuitRequestMessage(username, split[1]));
                                            break;
                                        case "quit" :
                                            ctx.channel().close();
                                            break;
                                    }
                                }

                            }, "system in").start();
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
        }
    }
}
