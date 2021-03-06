package com.changlu.server;

import com.changlu.protocol.MessageCodecSharable;
import com.changlu.protocol.ProcotolFrameDecoder;
import com.changlu.server.handler.*;
import com.changlu.server.session.Session;
import com.changlu.server.session.SessionFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatServer {
    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        //handler
        LoginRequestMessageHandler LOGIN_HANDLER = new LoginRequestMessageHandler();
        ChatRequestMessageHandler CHAT_HANDLER = new ChatRequestMessageHandler();
        GroupCreateRequestMessageHandler GROUP_CREATE_HANDLER = new GroupCreateRequestMessageHandler();
        GroupChatRequestMessageHandler GROUP_CHAT_HANDLER = new GroupChatRequestMessageHandler();
        GroupMembersRequestMessageHandler GROUP_MEMBERS_HANDLER = new GroupMembersRequestMessageHandler();
        GroupJoinRequestMessageHandler GROUP_JOIN_HANDLER = new GroupJoinRequestMessageHandler();
        GroupQuitRequestMessageHandler GROUP_QUIT_HANDLER = new GroupQuitRequestMessageHandler();
        QuitHandler QUIT_HANDLER = new QuitHandler();
        QuitMemberRequestMessageHandler QUIT_MEMBER_HANDLER = new QuitMemberRequestMessageHandler();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.group(boss, worker);
            serverBootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {

                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProcotolFrameDecoder());
                    ch.pipeline().addLast(LOGGING_HANDLER);
                    ch.pipeline().addLast(MESSAGE_CODEC);
                    //???????????????????????????????????????????????????????????????
                    // 5s ????????????????????? channel ??????????????????????????? IdleState#READER_IDLE ??????
                    ch.pipeline().addLast(new IdleStateHandler(5, 0, 0));
                    ch.pipeline().addLast(new ChannelDuplexHandler(){ // ???????????????????????????ChannelDuplexHandler ??????????????????????????????????????????
                        //????????????IdleStateHandler?????????
                        @Override
                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                            IdleStateEvent event = (IdleStateEvent) evt;
                            //???????????????
                            if (event.state() == IdleState.READER_IDLE) {
                                log.debug("??????5s????????????????????????");
                                ctx.channel().close();
                            }
                        }
                    });
                    ch.pipeline().addLast(LOGIN_HANDLER);//????????????handler
                    ch.pipeline().addLast(CHAT_HANDLER);//??????????????????handler
                    ch.pipeline().addLast(GROUP_CREATE_HANDLER);//????????????????????????handler
                    ch.pipeline().addLast(GROUP_CHAT_HANDLER);//???????????????????????????handler
                    ch.pipeline().addLast(GROUP_MEMBERS_HANDLER);//???????????????????????????????????????handler
                    ch.pipeline().addLast(GROUP_JOIN_HANDLER);//??????????????????handler
                    ch.pipeline().addLast(GROUP_QUIT_HANDLER);//??????????????????handler
                    ch.pipeline().addLast(QUIT_HANDLER);//?????????????????????handler
                    ch.pipeline().addLast(QUIT_MEMBER_HANDLER);//??????????????????handler
                }
            });
            Channel channel = serverBootstrap.bind(8080).sync().channel();
            log.debug("????????????????????????");
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("server error", e);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

}
