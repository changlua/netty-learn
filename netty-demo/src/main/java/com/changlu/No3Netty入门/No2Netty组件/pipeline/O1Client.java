package com.changlu.No3Netty入门.No2Netty组件.pipeline;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Scanner;

/**
 * @ClassName O1Client
 * @Author ChangLu
 * @Date 2022/1/6 14:04
 * @Description client：用于向服务端发起请求，可以自由输入信息发送出去，q表示退出当前连接
 */
@Slf4j
public class O1Client {

    public static void main(String[] args) throws InterruptedException {
        final ChannelFuture future = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel channel) throws Exception {
                        channel.pipeline().addLast(new StringDecoder());
                        channel.pipeline().addLast(new LoggingHandler());
                        channel.pipeline().addLast(new StringEncoder());
                        channel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.debug("接收到来自 {} 数据：{}", ctx.channel(), msg);
                            }
                        });

                    }
                })
                .connect(new InetSocketAddress("localhost", 8080));
        //等待连接
        future.sync();
        final Channel channel = future.channel();
        log.debug("成功连接：{}", channel);
        log.debug("请输入消息或者q退出成功:");
        new Thread(()->{
            final Scanner scanner = new Scanner(System.in);
            while (true) {
                final String msg = scanner.nextLine();
                if ("q".equals(msg)){
                    channel.close();
                    break;
                }
                channel.writeAndFlush(msg);
            }
        }).start();

    }

}