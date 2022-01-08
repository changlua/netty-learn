package com.changlu.No4Netty进阶.No1粘包与半包.解决方案.No2定长解码器;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.logging.LoggingHandler;

/**
 * @ClassName Server
 * @Author ChangLu
 * @Date 2022/1/8 14:57
 * @Description Server：黏包现象复现
 */
public class Server {

    public static void main(String[] args) throws InterruptedException {
        new ServerBootstrap()
                .group(new NioEventLoopGroup(), new NioEventLoopGroup(2))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) {
                        //使用定长解码器：固定设置为10个字节，也就是每次读事件都会拿到10个字节长度的ByteBuf
                        ch.pipeline().addLast(new FixedLengthFrameDecoder(10));
                        ch.pipeline().addLast(new LoggingHandler());
                    }
                })
                .bind(8080).sync();
        System.out.println("服务器启动成功！");
    }

}