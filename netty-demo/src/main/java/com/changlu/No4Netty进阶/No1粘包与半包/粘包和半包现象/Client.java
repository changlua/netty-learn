package com.changlu.No4Netty进阶.No1粘包与半包.粘包和半包现象;

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
 * @Description Client：客户端连接，批量发送数据
 */
@Slf4j
public class Client {
    public static void main(String[] args) throws InterruptedException {
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
                                //连续发送10次16字节的内容
                                for (int i = 0; i < 10; i++) {
                                    final ByteBuf buffer = ctx.alloc().buffer(16);
                                    buffer.writeBytes(new byte[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16});
                                    ctx.writeAndFlush(buffer);
                                }
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