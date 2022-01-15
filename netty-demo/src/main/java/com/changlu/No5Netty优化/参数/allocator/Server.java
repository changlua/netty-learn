package com.changlu.No5Netty优化.参数.allocator;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @ClassName Server
 * @Author ChangLu
 * @Date 2022/1/15 21:56
 * @Description 测试
 */
public class Server {

    public static void main(String[] args) throws InterruptedException {
        final LoggingHandler loggingHandler = new LoggingHandler();
        new ServerBootstrap()
                .group(new NioEventLoopGroup(),new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(loggingHandler);
                    }
                }).bind(8081).sync();
        System.out.println("服务器已启动!");
    }

}