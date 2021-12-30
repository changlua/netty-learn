package com.changlu.No3Netty入门.No1HelloWorld;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

/**
 * @ClassName NettyServer
 * @Author ChangLu
 * @Date 2021/12/28 22:26
 * @Description 基于Netty的服务器
 */
public class NettyServer {

    public static void main(String[] args) {
        //1、服务器端的启动器，负责组装netty组件，启动服务器
        new ServerBootstrap()
                // 2、BossEventLoop，WorkerEventLoop（selector+thread=>eventLoop，两个组成处理循环事件）
                // Group：组的意思，包含了线程和选择器
                .group(new NioEventLoopGroup())
                // 3、设置服务器channel实现（包含OIO、BIO）；这里NioServerSocketChannel是对原生的ServerSocketChannel进行了封装
                // 在netty中提供了多个ServerSocketChannel的实现
                .channel(NioServerSocketChannel.class)
                // 4、处理分工  boss负责处理连接 worker(child)处理读写。在这里决定了之后worker要干哪一些事情（具体某个事情抽象成处理器，也就是handler）
                .childHandler(
                        // 5、代表和客户端进行数据读写的通道 Initializer 初始化 负责添加别的handler
                    new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        //6、添加具体handler。
                        // StringDecoder：目的就是将ByteBuf数据类型转换为String字符串
                        ch.pipeline().addLast(new StringDecoder());
                        // ChannelInboundHandlerAdapter：自定义handler
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){

                            //channelRead：表示要处理读事件。这里的msg对象就是转换之后的字符串
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                System.out.println(msg);//将转换后的字符串打印出来！
                            }
                        });
                    }
                })
                // 7、指定了NioServerSocketChannel启动后绑定的监听端口
                .bind(8080);
    }

}