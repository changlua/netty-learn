package com.changlu.No4Netty进阶.No2协议设计与解析.No1常用协议示例.No1Redis示例;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

/**
 * @ClassName Test
 * @Author ChangLu
 * @Date 2022/1/9 13:32
 * @Description 模拟Redis客户端来向redis服务端发送一条命令
 */
@Slf4j
public class Test {

    public static void main(String[] args) throws InterruptedException {
        byte[] LINE = {13, 10};//两个字节表示回车，换行
        NioEventLoopGroup group = new NioEventLoopGroup();
        Channel channel = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelActive(ChannelHandlerContext ctx){
                                //向redis服务端发送一个写指令：set name changlu
                                set(ctx);
                                //向redis服务端发送一个读指令：get name
                                get(ctx);
                            }

                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf buf = (ByteBuf) msg;
                                log.debug("收到 {} , 消息为：{}", ctx.channel(), buf.toString(Charset.defaultCharset()));
                                super.channelRead(ctx, msg);
                            }

                            //执行set命令：set name changlu
                            private void set(ChannelHandlerContext ctx){
                                final ByteBuf buffer = ctx.alloc().buffer();
                                buffer.writeBytes("*3".getBytes());
                                buffer.writeBytes(LINE);
                                buffer.writeBytes("$3".getBytes());
                                buffer.writeBytes(LINE);
                                buffer.writeBytes("set".getBytes());
                                buffer.writeBytes(LINE);
                                buffer.writeBytes("$4".getBytes());
                                buffer.writeBytes(LINE);
                                buffer.writeBytes("name".getBytes());
                                buffer.writeBytes(LINE);
                                buffer.writeBytes("$7".getBytes());
                                buffer.writeBytes(LINE);
                                buffer.writeBytes("changlu".getBytes());
                                buffer.writeBytes(LINE);
                                ctx.writeAndFlush(buffer);
                            }

                            //执行get命令：get name
                            private void get(ChannelHandlerContext ctx){
                                final ByteBuf buffer = ctx.alloc().buffer();
                                buffer.writeBytes("*2".getBytes());
                                buffer.writeBytes(LINE);
                                buffer.writeBytes("$3".getBytes());
                                buffer.writeBytes(LINE);
                                buffer.writeBytes("get".getBytes());
                                buffer.writeBytes(LINE);
                                buffer.writeBytes("$4".getBytes());
                                buffer.writeBytes(LINE);
                                buffer.writeBytes("name".getBytes());
                                buffer.writeBytes(LINE);
                                ctx.writeAndFlush(buffer);
                            }

                        });
                    }
                }).connect("127.0.0.1", 6379).sync().channel();
        log.debug("客户端连接成功：{}", channel);
        channel.closeFuture().addListener(future -> {
            group.shutdownGracefully();
        });
    }

}