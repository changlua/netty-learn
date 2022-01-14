package com.changlu.client;

import com.changlu.message.*;
import com.changlu.protocol.MessageCodecSharable;
import com.changlu.protocol.ProcotolFrameDecoder;
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
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class ChatClient {
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        //计数器
        CountDownLatch WAIT_FOR_LOGIN = new CountDownLatch(1);
        AtomicBoolean LOGIN = new AtomicBoolean(false);//检测登陆变量
        AtomicBoolean EXIT = new AtomicBoolean(false);//检测断开连接变量
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
                    //客户端，主要用来判断是不是写空闲时间过长，来进行发送心跳，表示当前用户并没有断开
                    // 3s 内如果没有收到 channel 的数据，会触发一个 IdleState#READER_IDLE 事件
                    ch.pipeline().addLast(new IdleStateHandler(0, 3, 0));
                    ch.pipeline().addLast(new ChannelDuplexHandler(){ // 使用一个双向处理器ChannelDuplexHandler 可以同时作为入站和出站处理器
                        //用来捕获IdleStateHandler的事件
                        @Override
                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                            IdleStateEvent event = (IdleStateEvent) evt;
                            //触发读事件
                            if (event.state() == IdleState.WRITER_IDLE) {
//                                log.debug("已经3s没有读到数据了！");
                                ctx.channel().writeAndFlush(new PingMessage());
                            }
                        }
                    });
                    ch.pipeline().addLast("client handler", new ChannelInboundHandlerAdapter(){

                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//                            log.debug("msg: {}", msg);
                            if (msg instanceof LoginResponseMessage) {
                                LoginResponseMessage response = (LoginResponseMessage) msg;
                                if (response.isSuccess()){
                                    LOGIN.set(true);
                                }
                                WAIT_FOR_LOGIN.countDown();
                            }
                        }

                        // 在连接断开时触发
                        @Override
                        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                            log.debug("连接已断开，请按任意键退出...");
                            EXIT.set(true);
                        }

                        // 在出现异常时触发
                        @Override
                        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                            log.debug("连接已断开，请按任意键退出...，异常信息：{}",cause.getMessage());
                            EXIT.set(true);
                        }

                        @Override
                        public void channelActive(ChannelHandlerContext ctx){
                            //负责接收用户在控制台上的输入，负责向服务器发送数据
                            new Thread(()->{
                                Scanner scanner = new Scanner(System.in);
                                System.out.println("请输入用户名：");
                                String username = scanner.nextLine();
                                if (EXIT.get()){
                                    return;
                                }
                                System.out.println("请输入密码：");
                                String password = scanner.nextLine();
                                if (EXIT.get()){
                                    return;
                                }
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
                                    System.out.println("quitmember [username]");
                                    System.out.println("quit");
                                    System.out.println("==================================");
                                    String command = scanner.nextLine();
                                    if (EXIT.get()){
                                        return;
                                    }
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
                                            users.add(username);//将自己也添加到群聊中
                                            ctx.writeAndFlush(new GroupCreateRequestMessage(split[1],users));
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
                                        case "quitmember":
                                            ctx.writeAndFlush(new QuitMemberRequestMessage(split[1]));
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
