package com.changlu.No3Netty入门.No2Netty组件.demos.echoserverDemo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

/**
 * @ClassName Server
 * @Author ChangLu
 * @Date 2022/1/8 9:42
 * @Description echoserver：提供回显服务的服务器，就是收到什么，然后就发送什么的程序。
 */
@Slf4j
public class Server {

    public static void main(String[] args) throws InterruptedException {
        final NioEventLoopGroup boss = new NioEventLoopGroup();
        final NioEventLoopGroup worker = new NioEventLoopGroup(2);
        new ServerBootstrap()
                .group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf buf = (ByteBuf)msg;
                                log.debug("收到客户端发送数据：{}", buf.toString(Charset.defaultCharset()));
                                final ByteBuf response = ctx.alloc().buffer();
                                response.writeBytes(buf);
                                //向客户端回发数据：需要手动释放
                                ctx.writeAndFlush(response).addListener((future)->{
                                    //释放ByteBuf
                                    ReferenceCountUtil.release(response);
                                });
                                //向后传递让Tail handler来进行释放msg
                                super.channelRead(ctx, msg);
                            }
                        });
                    }
                })
                .bind(8080).sync();
        System.out.println("服务器启动成功！");
    }

}