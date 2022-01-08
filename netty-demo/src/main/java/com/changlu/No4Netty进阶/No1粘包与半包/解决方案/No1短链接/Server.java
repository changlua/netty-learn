package com.changlu.No4Netty进阶.No1粘包与半包.解决方案.No1短链接;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
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
                //设置ByteBuf缓冲区为16字节
                .childOption(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(16, 16, 16))
                .group(new NioEventLoopGroup(), new NioEventLoopGroup(2))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) {
                        //添加日志处理器（会打印每次接收包得到的数据）
                        ch.pipeline().addLast(new LoggingHandler());
                    }
                })
                .bind(8080).sync();
        System.out.println("服务器启动成功！");
    }

}