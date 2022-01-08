package com.changlu.No4Netty进阶.No1粘包与半包.解决方案.No1短链接;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;


/**
 * @ClassName Client
 * @Author ChangLu
 * @Date 2022/1/8 9:49
 * @Description 短链接：发送一次请求数据，就重新断开重启，以连接、重启作为消息分割
 */
@Slf4j
public class Client {
    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            send();
        }
    }

    private static void send() throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        Channel channel = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder());//String=>ByteBuf
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {

                            //channelActive：连接建立之后会执行会触发Active事件
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                //发送16个字节
                                final ByteBuf buffer = ctx.alloc().buffer(16);
                                buffer.writeBytes(new byte[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16});
                                ctx.writeAndFlush(buffer);
                                ctx.channel().close();
                                group.shutdownGracefully();
                                log.debug("finish!");
                            }
                        });
                    }
                }).connect("127.0.0.1", 8080).sync().channel();
        log.debug("客户端连接成功：{}", channel);
        channel.closeFuture().addListener(future -> {
            group.shutdownGracefully();
        });
    }
}