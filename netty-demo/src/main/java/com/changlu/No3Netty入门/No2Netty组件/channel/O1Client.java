package com.changlu.No3Netty入门.No2Netty组件.channel;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @ClassName NettyClient
 * @Author ChangLu
 * @Date 2021/12/28 22:26
 * @Description 测试connect的连接问题
 */
@Slf4j
public class O1Client {

    public static void main(String[] args) throws Exception{
        ChannelFuture channelFuture = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                //connect是一个异步非阻塞方法，返回的是一个ChannelFuture，专门用于记录异步方法状态的。
                .connect(new InetSocketAddress("localhost", 8080));
        //阻塞方法，直到连接建立之后再会停止阻塞继续向下执行。
        // 若是不调用该方法，直接去获取channel来发送数据，很有可能因为没有建立好连接导致发送失败
        channelFuture.sync();
        Channel channel = channelFuture.channel();
        log.info("channel {}",channel);
        //测试：channel.writeAndFlush("hello")
        channel.writeAndFlush("hello");
        System.out.println();
    }

}