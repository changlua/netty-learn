package com.changlu.No3Netty入门.No2Netty组件.channel;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.DefaultEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

/**
 * @ClassName EventLoopServer
 * @Author ChangLu
 * @Date 2022/1/2 22:19
 * @Description 服务端
 */
@Slf4j
public class O1Server {

    public static void main(String[] args) {
        //分工细化2：若是执行事件的过程中某个事件耗时较长，那么可以将其提交给其他事件组来进行异步执行
        //这里handler2进行处理的操作会提交给该组来进行执行
        DefaultEventLoop group = new DefaultEventLoop();
        new ServerBootstrap()
                //分工细化1：Boss对应一个组(不用传递参数也没事)，负责NioServerSocketChannel的accept监听;
                //          worker对应一个组，之后来临连接的channel都会绑定其某个EventLoop
                .group(new NioEventLoopGroup(),new NioEventLoopGroup(2))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast("handler1",new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf buf = (ByteBuf) msg;
                                log.debug(buf.toString(Charset.defaultCharset()));//打印接收到的字符串
                                //传递给下一个handler执行，若是不调用无法传递
                                ctx.fireChannelRead(msg);
                            }
                        })//分工细化2：指定group组来进行异步执行
                                .addLast(group, "handler2", new ChannelInboundHandlerAdapter(){
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        ByteBuf buf = (ByteBuf) msg;
                                        log.debug(buf.toString(Charset.defaultCharset()));//打印接收到的字符串
                                    }
                                });
                    }
                })
                .bind(8080);
    }

}