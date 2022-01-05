package com.changlu.No3Netty入门.No2Netty组件.channel;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName O3Server
 * @Author ChangLu
 * @Date 2022/1/5 16:43
 * @Description 用于接收03client案例发起的连接
 */
@Slf4j
public class O3Server {

    public static void main(String[] args) {
        new ServerBootstrap()
                .group(new NioEventLoopGroup(),new NioEventLoopGroup(2))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel sc) throws Exception {
                        sc.pipeline().addLast(new StringDecoder());
                        sc.pipeline().addLast(new ChannelInboundHandlerAdapter(){

                            @Override
                            public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
                                log.debug("成功建立连接，channel {}",ctx.channel());
                            }

                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.debug("收到消息，来自 channel {}，数据为 {}",ctx.channel(), msg);
                            }
                        });
                    }
                })
                .bind(8080);

    }

}