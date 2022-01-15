package com.changlu.No5Netty优化.参数.SO_BACKLOG;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @ClassName Server
 * @Author ChangLu
 * @Date 2022/1/15 21:56
 * @Description SO_BACKLOG：全连接队列中允许存放连接的个数
 */
public class Server {

    public static void main(String[] args) throws InterruptedException {
        new ServerBootstrap()
                .group(new NioEventLoopGroup(),new NioEventLoopGroup())
                .option(ChannelOption.SO_BACKLOG, 2) //设置全连接队列个数为2
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                    }
                }).bind(8081).sync();
        System.out.println("服务器已启动!");
    }

}