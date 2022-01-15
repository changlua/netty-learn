package com.changlu.No5Netty优化.参数.rcvbuf_allocator;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName Server
 * @Author ChangLu
 * @Date 2022/1/15 21:56
 * @Description 测试
 */
@Slf4j
public class Server {

    public static void main(String[] args) throws InterruptedException {
        final LoggingHandler loggingHandler = new LoggingHandler();
        new ServerBootstrap()
                .group(new NioEventLoopGroup(),new NioEventLoopGroup())
                //设置指定的接收ByteBuf大小为100字节
                .childOption(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(100))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(loggingHandler);
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.debug("msg => {}", msg);
                            }
                        });
                    }
                }).bind(8082).sync();
        System.out.println("服务器已启动!");
    }

}