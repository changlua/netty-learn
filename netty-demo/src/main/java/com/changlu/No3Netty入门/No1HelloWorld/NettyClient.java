package com.changlu.No3Netty入门.No1HelloWorld;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;

/**
 * @ClassName NettyClient
 * @Author ChangLu
 * @Date 2021/12/28 22:26
 * @Description 基于netty的客户端。注意：调试时要回车一下才能够发送出数据！
 */
public class NettyClient {

    public static void main(String[] args) throws Exception{
        // 1、启动类
        new Bootstrap()  //也可以使用之前NIO、BIO的连接客户端进行连接，只不过这里是netty的demo也就使用EventLoop来演示
                // 2、添加EventLoop
                .group(new NioEventLoopGroup())
                // 3、选择客户端channel实现
                .channel(NioSocketChannel.class)
                // 4、添加处理器
                .handler(new ChannelInitializer<NioSocketChannel>() {

                    // 连接建立后就会执行这个初始化方法
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        // 同时也添加一个编码器。把String=>ByteBuf 发送出去
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                // 5、连接到服务器
                .connect(new InetSocketAddress("localhost",8080))
                .sync()
                .channel()
                // 6、向服务器发送数据
                .writeAndFlush("hello,world!");
    }

}