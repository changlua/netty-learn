package com.changlu.No3Netty入门.No2Netty组件.pipeline;

import com.google.common.base.Charsets;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName O2InboundHandlerTest
 * @Author ChangLu
 * @Date 2022/1/6 14:56
 * @Description InboundHandler测试：handler之间传递规则，各个handler进行数据处理分工
 */
@Slf4j
public class O2InboundHandlerTest {

    public static void main(String[] args) throws InterruptedException {
        new ServerBootstrap()
                .group(new NioEventLoopGroup(),new NioEventLoopGroup(2))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        //添加入站事件
                        //第一个handler：将ByteBuf => String
                        ch.pipeline().addLast("h1", new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.debug("1（in）");
                                ByteBuf buf = (ByteBuf)msg;
                                final String data = buf.toString(Charsets.UTF_8);
                                super.channelRead(ctx, data);//方式一：执行下一个handler
                            }
                        });
                        //第二个handler：将String封装到Result对象中
                        ch.pipeline().addLast("h2", new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.debug("2（in）");
                                final Result result = new Result("小明", (String) msg);
                                ctx.fireChannelRead(result);//方式二：同样执行下一个handler
                            }
                        });
                        //第三个handler：接受到Result对象输出
                        ch.pipeline().addLast("h3", new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.debug("3（in）");
                                log.debug("解析得到的数据为：{}", msg);
                            }
                        });
                    }
                })
                .bind(8080)
                .sync();
        log.debug("服务器启动成功！");
    }

    @Data
    @AllArgsConstructor
    static class Result{
        private String name;
        private String msg;
    }

}