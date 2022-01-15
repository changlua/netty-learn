package com.changlu.No5Netty优化.参数.CONNECT_TIMEOUT_MILLIS;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @ClassName Server
 * @Author ChangLu
 * @Date 2022/1/15 17:33
 * @Description 服务器：用于提供连接
 */
public class Server {
    public static void main(String[] args) throws InterruptedException {
        new ServerBootstrap()
                .group(new NioEventLoopGroup(),new NioEventLoopGroup(2))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                    }
                }).bind(8081).sync();
        System.out.println("服务器已启动!");
    }
}