package com.changlu.No3Netty入门.No2Netty组件.channel;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @ClassName O2handleResultClient
 * @Author ChangLu
 * @Date 2022/1/3 23:07
 * @Description 处理连接操作(异步)：对于异步连接包含两种处理方式（同步阻塞等待、异步）
 */
@Slf4j
public class O2handleConnectClient {

    public static void main(String[] args) throws Exception{
        new NioEventLoopGroup().shutdownGracefully();
        ChannelFuture channelFuture = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                .connect(new InetSocketAddress("localhost", 8080));
        //方式一：同步阻塞等待连接
        //阻塞方法，直到连接建立之后再会停止阻塞继续向下执行。
        // 若是不调用该方法，直接去获取channel来发送数据，很有可能因为没有建立好连接导致发送失败
//        channelFuture.sync();
//        Channel channel = channelFuture.channel();
//        log.info("channel {}",channel);
        //测试：channel.writeAndFlush("hello")

        //方式二：添加一个监听器，来异步处理结果
        channelFuture.addListener(new ChannelFutureListener() {
            //当连接完成就会执行该回调方法：执行完成事件，其中channelFuture就是本身对象
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                Channel channel = channelFuture.channel();
                log.info("channel {}",channel);
                channel.writeAndFlush("hello!");
            }
        });

    }


}