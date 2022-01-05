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
import java.util.Scanner;

/**
 * @ClassName O3handleCloseClient
 * @Author ChangLu
 * @Date 2022/1/5 16:31
 * @Description 处理关闭channel连接(异步)：同样是同步、异步方法解决
 */
@Slf4j
public class O3handleCloseClient {

    public static void main(String[] args) throws InterruptedException {
        final NioEventLoopGroup group = new NioEventLoopGroup();
        final ChannelFuture future = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel channel) throws Exception {
                        channel.pipeline().addLast(new StringEncoder());
                    }
                })
                .connect(new InetSocketAddress("localhost", 8080));
        final Channel channel = future.sync().channel();
        log.debug("channel连接已建立 {}", channel);
        //创建一个线程来处理用户操作
        new Thread(()->{
            final Scanner scanner = new Scanner(System.in);
            while(true){
                final String line = scanner.nextLine();
                if ("q".equals(line)) {
                    //关闭连接
                    final ChannelFuture closeFuture = channel.close();
//                    //方式一：同步关闭（阻塞等待）
//                    try {
//                        closeFuture.sync();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    //阻塞结束则表示成功关闭
//                    log.debug("连接已关闭！");
//                    //整个程序此时并没有关闭，仅仅只是断开了该channel连接，若要是想让程序直接结束，需要将事件循环组进行关闭！
//                    group.shutdownGracefully();
                    break;
                }
                channel.writeAndFlush(line);
            }
        }).start();

        //方式2：异步处理关闭结果
        final ChannelFuture closeFuture = channel.closeFuture();
        //添加监听器
        closeFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                //阻塞结束则表示成功关闭
                log.debug("连接已关闭！");
                group.shutdownGracefully();//关闭事件循环组，结束程序
            }
        });
    }

}