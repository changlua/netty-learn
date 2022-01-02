package com.changlu.No3Netty入门.No2Netty组件.EventLoop;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;

/**
 * @ClassName NettyClient
 * @Author ChangLu
 * @Date 2021/12/28 22:26
 * @Description 客户端：注意debug进行测试的时候要设置thread，不然的话debug进行发送数据对应发数据的线程会阻塞
 */
public class O2Client {

    public static void main(String[] args) throws Exception{
        final Channel channel = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                .connect(new InetSocketAddress("localhost", 8080))
                .sync()
                .channel();
        System.out.println(channel);
        //测试：channel.writeAndFlush("hello")
        System.out.println();
    }

}