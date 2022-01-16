package com.changlu.client;

import com.changlu.message.LoginRequestMessage;
import com.changlu.message.rpc.RpcRequestMessage;
import com.changlu.protocol.MessageCodecSharable;
import com.changlu.protocol.ProcotolFrameDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName RpcClient
 * @Author ChangLu
 * @Date 2022/1/16 21:45
 * @Description RPC远程调用客户端
 */
@Slf4j
public class RpcClient {

    public static void main(String[] args) {
        MessageCodecSharable messageCodec = new MessageCodecSharable();
        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            final ChannelFuture future = new Bootstrap()
                    .group(worker)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
//                            ch.pipeline().addLast(new StringEncoder());
                            ch.pipeline().addLast(new ProcotolFrameDecoder());
                            ch.pipeline().addLast(loggingHandler);
                            ch.pipeline().addLast(messageCodec);
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    final RpcRequestMessage message = new RpcRequestMessage(
                                            1,
                                            "com.changlu.server.service.rpc.RpcService",
                                            "sayHello",
                                            String.class,
                                            new Class[]{String.class},
                                            new Object[]{"changlu"}
                                    );
                                    ctx.channel().writeAndFlush(message);
//                                    ctx.channel().writeAndFlush(new LoginRequestMessage("changlu", "123"));
                                }
                            });
                        }
                    }).connect("127.0.0.1", 8080).sync();
            log.debug("客户端连接成功！");
            ChannelFuture closeFuture = future.channel().closeFuture();
            closeFuture.addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    log.debug("客户端关闭连接！");
                    worker.shutdownGracefully();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}