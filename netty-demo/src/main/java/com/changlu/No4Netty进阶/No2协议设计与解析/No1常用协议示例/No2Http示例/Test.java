package com.changlu.No4Netty进阶.No2协议设计与解析.No1常用协议示例.No2Http示例;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;


/**
 * @ClassName Test
 * @Author ChangLu
 * @Date 2022/1/9 14:01
 * @Description HTTP协议示例：测试协议的编解码
 */
@Slf4j
public class Test {
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup(2);
        new ServerBootstrap()
                .group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
//                        ch.pipeline().addLast(new LoggingHandler());
                        //netty提供的对HTTP协议编解码处理器类
                        ch.pipeline().addLast(new HttpServerCodec());
                        //②若是只对HTTP请求的请求头感兴趣，那么实现SimpleChannelInboundHandler实例,指明感兴趣的请求对象为HttpRequest（实际就是DefaultHttpRequest）
                        ch.pipeline().addLast(new SimpleChannelInboundHandler<HttpRequest>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, HttpRequest msg) throws Exception {
                                log.debug("解析对象类型：{}", msg.getClass());
                                log.debug(msg.uri());
                                //进行响应返回
                                //①构建响应对象
                                final DefaultFullHttpResponse response =
                                        new DefaultFullHttpResponse(msg.protocolVersion(), HttpResponseStatus.OK);
                                // 响应内容
                                final byte[] content = "<h1>Hello,world!</h1>".getBytes();
                                //设置响应头：content-length：内容长度。不设置的话浏览器就不能够知道确切的响应内容大小则会造成一直没有处理完的现象
                                response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, content.length);
                                response.content().writeBytes(content);
                                //②写会响应
                                ctx.writeAndFlush(response);
                            }
                        });

                        //①对于HTTP一次请求会解析成两个对象，每个对象会走一次channelRead方法（也就是说该方法会执行两次，每次得到的msg是一个对象）
//                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
//                            @Override
//                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
////                                log.debug("msg.getClass() => {}", msg.getClass());
//                                if (msg instanceof HttpRequest){
//                                    System.out.println("请求行、头");
//                                }else if (msg instanceof HttpContent){
//                                    System.out.println("请求体");
//                                }
//                                super.channelRead(ctx, msg);
//                            }
//                        });
                    }
                }).bind(8080);
        log.debug("服务器启动成功！");
    }
}