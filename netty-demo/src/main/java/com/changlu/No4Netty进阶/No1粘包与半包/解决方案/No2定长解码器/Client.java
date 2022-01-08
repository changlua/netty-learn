package com.changlu.No4Netty进阶.No1粘包与半包.解决方案.No2定长解码器;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Random;


/**
 * @ClassName Client
 * @Author ChangLu
 * @Date 2022/1/8 9:49
 * @Description 短链接：发送一次请求数据，就重新断开重启，以连接、重启作为消息分割
 */
@Slf4j
public class Client {
    public static void main(String[] args) throws InterruptedException {
        send();
    }

    private static void send() throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        Channel channel = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder());
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                ByteBuf buffer = ctx.alloc().buffer();
                                char c = '0';
                                final Random r = new Random();
                                //生成10个定长的字节数组全部写入到一个ByteBuf中，最后发送出去
                                //目的：检验服务器能否每次取到定长ByteBuf
                                for (int i = 0; i < 10; i++) {
                                    final byte[] bytes = fill10Bytes(c, r.nextInt(10) + 1);
                                    buffer.writeBytes(bytes);
                                    c++;
                                }
                                ctx.channel().writeAndFlush(buffer);
                            }
                        });
                    }
                }).connect("127.0.0.1", 8080).sync().channel();
        log.debug("客户端连接成功：{}", channel);
        channel.closeFuture().addListener(future -> {
            group.shutdownGracefully();
        });
    }

    /**
     * 生成len个指定c字符，定长为10，多余部分使用"_"填充
     * @param c 填充字符
     * @param len 字符长度
     * @return 填充好的字节数组
     */
    public static byte[] fill10Bytes(char c, int len){
        final byte[] bytes = new byte[10];
        Arrays.fill(bytes, (byte) '_');
        for (int i = 0; i < len; i++) {
            bytes[i] = (byte) c;
        }
        System.out.println(new String(bytes));
        return bytes;
    }

}