package com.changlu.No3Netty入门.No2Netty组件.pipeline;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName O3OutBoundHandlerTest
 * @Author ChangLu
 * @Date 2022/1/6 15:19
 * @Description 出站处理器：ctx调用时outhandler执行顺序，普通channel输出数据时outhandler执行顺序
 */
@Slf4j
public class O3OutBoundHandlerTest {

    public static void main(String[] args) throws InterruptedException {
        new ServerBootstrap()
                .group(new NioEventLoopGroup(),new NioEventLoopGroup(2))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringDecoder());
                        ch.pipeline().addLast(new StringEncoder());
                        //添加入站事件
                        ch.pipeline().addLast("h1", new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.debug("1（in）");
                                log.debug("收到数据，{}", msg);
                                super.channelRead(ctx, msg);
                                //向客户端写数据
                                //方式一：调用NioSocketChannel来进行发送数据。（从tail末尾向前依次执行outhandler）
//                                ch.writeAndFlush("hello,client!");
                                //方式二：调用ctx来进行发送数据。（从当前handler向前依次执行outhandler）
                                ctx.writeAndFlush("hello,client");
                            }
                        });
                        //出站自定义的三道工序
                        ch.pipeline().addLast("h4", new ChannelOutboundHandlerAdapter(){
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.debug("4（out）");
                                super.write(ctx, msg, promise);
                            }
                        });
                        ch.pipeline().addLast("h5", new ChannelOutboundHandlerAdapter(){
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.debug("5（out）");
                                super.write(ctx, msg, promise);
                            }
                        });
                        ch.pipeline().addLast("h6", new ChannelOutboundHandlerAdapter(){
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.debug("6（out）");
                                super.write(ctx, msg, promise);
                            }
                        });
                    }
                })
                .bind(8080)
                .sync();
        log.debug("服务器启动成功！");
    }

}