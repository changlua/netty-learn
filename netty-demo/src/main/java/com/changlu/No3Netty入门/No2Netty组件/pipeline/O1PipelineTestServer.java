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
 * @ClassName PipelineTest
 * @Author ChangLu
 * @Date 2022/1/6 13:59
 * @Description Pipeline添加入站、出站handler：入站、出站时handler的执行顺序
 */
@Slf4j
public class O1PipelineTestServer {

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
                                super.channelRead(ctx, msg);//调用下一条执行链：底层执行了ctx.fireChannelRead(msg);
                            }
                        });
                        ch.pipeline().addLast("h2", new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.debug("2（in）");
                                super.channelRead(ctx, msg);//调用下一条执行链：底层执行了ctx.fireChannelRead(msg);
                            }
                        });
                        ch.pipeline().addLast("h3", new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.debug("3（in）");
                                super.channelRead(ctx, msg);//调用下一条执行链：底层执行了ctx.fireChannelRead(msg);
                                //接收到数据之后来进行写数据（紧接着会触发出站handler）
                                ch.writeAndFlush("hello，client！");
//                                ch.writeAndFlush(ctx.alloc().buffer().writeBytes("hello,client".getBytes()));//或者直接自己将String转换为ByteBuf发送出去
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