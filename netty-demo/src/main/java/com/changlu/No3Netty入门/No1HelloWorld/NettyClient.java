package com.changlu.No3Netty入门.No1HelloWorld;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

import java.util.Date;

/**
 * @ClassName NettyClient
 * @Author ChangLu
 * @Date 2021/12/28 22:26
 * @Description 基于netty的客户端
 */
public class NettyClient {

    public static void main(String[] args) throws Exception{
        new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(new StringDecoder());
                    }
                })
                .connect("127.0.0.1",8080)
                .sync()
                .channel()
                .writeAndFlush(new Date() + "hello,world!");
    }

}