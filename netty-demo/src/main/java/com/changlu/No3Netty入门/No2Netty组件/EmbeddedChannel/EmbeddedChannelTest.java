package com.changlu.No3Netty入门.No2Netty组件.EmbeddedChannel;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.channel.embedded.EmbeddedChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName EmbeddedChannelTest
 * @Author ChangLu
 * @Date 2022/1/6 16:04
 * @Description EmbeddedChannel：工具类，能够快速测试我们所写的一些入站、出站handler执行顺序及过程
 */
@Slf4j
public class EmbeddedChannelTest {

    public static void main(String[] args) {
        final ChannelInboundHandlerAdapter h1 = new ChannelInboundHandlerAdapter(){
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                log.debug("1（in）");
                super.channelRead(ctx, msg);
            }
        };
        final ChannelInboundHandlerAdapter h2 = new ChannelInboundHandlerAdapter(){
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                log.debug("2（in）");
                super.channelRead(ctx, msg);
            }
        };
        final ChannelOutboundHandlerAdapter h3 = new ChannelOutboundHandlerAdapter() {
            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                log.debug("3（out）");
                super.write(ctx, msg, promise);
            }
        };
        final ChannelOutboundHandlerAdapter h4 = new ChannelOutboundHandlerAdapter() {
            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                log.debug("4（out）");
                super.write(ctx, msg, promise);
            }
        };

        //初始化EmbeddedChannel
        final EmbeddedChannel channel = new EmbeddedChannel(h1, h2, h3, h4);
        //模拟入站操作
//        channel.writeInbound(ByteBufAllocator.DEFAULT.buffer().writeBytes("hello,server".getBytes()));
        //模拟出站操作
        channel.writeOutbound(ByteBufAllocator.DEFAULT.buffer().writeBytes("hello,client".getBytes()));
    }

}