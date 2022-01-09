package com.changlu.No4Netty进阶.No2协议设计与解析.No1常用协议示例.No3自定义协议示例.Test;

import com.changlu.No4Netty进阶.No2协议设计与解析.No1常用协议示例.No3自定义协议示例.Sharable注解编解码器使用.MessageCodec2;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

/**
 * @ClassName TestMessageCodec
 * @Author ChangLu
 * @Date 2022/1/9 18:04
 * @Description TODO
 */
@Slf4j
public class TestMessageCodec {
    public static void main(String[] args) throws Exception {
        //案例1：自定义编解码器测试
//        test01();
        //案例2：解码出现半包问题及解决方案，这里仅演示解码情况。
        test02();
    }

    /**
     * 案例1：自定义编解码器测试
     */
    public static void test01() throws Exception {
        final EmbeddedChannel channel = new EmbeddedChannel(new LoggingHandler(), new MessageCodec());
        //入站方法测试（编码）：encode()
        final LoginRequestMessage message = new LoginRequestMessage("changlu", "123456");
        channel.writeOutbound(message);
        //出站方法测试（解码）：decode
        final ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        new MessageCodec().encode(null, message, buffer);//根据协议来进行编码到ByteBuf中
        final ArrayList<Object> list = new ArrayList<>();
        new MessageCodec().decode(null, buffer, list);//之后对按照协议进行编码的ByteBuf进行解码取得一系列参数及对象
        log.debug("解码得到的对象是：{}", list.get(0));
    }

    /**
     * 案例2：解码出现半包问题及解决方案，这里仅演示解码情况
     *  问题描述：若是出现半包问题，那么可能就会出现接解析序列化异常！
     *  解决方案：使用LTC（基于长度的帧解码器）来解决半包、黏包问题。
     */
    public static void test02() throws Exception {
        //可将两个安全的执行器进行抽离
        final LoggingHandler loggingHandler = new LoggingHandler();
        final MessageCodec2 messageCodec2 = new MessageCodec2();
        final EmbeddedChannel channel = new EmbeddedChannel(
                loggingHandler,
                new LengthFieldBasedFrameDecoder(1024, 12, 4, 0, 0),
                messageCodec2 //自定义的编解码器
        );
        final LoginRequestMessage message = new LoginRequestMessage("changlu", "123456");
        final ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        new MessageCodec().encode(null, message, buffer);//根据协议来进行编码到ByteBuf中

        //进行切片将数据切分为两块（问题产生源头：此时就会出现半包问题，那么接序列化就会失败）
        final ByteBuf firBuf = buffer.slice(0, 100);
        final ByteBuf secBuf = buffer.slice(100, buffer.readableBytes() - 100);

        final ArrayList<Object> list = new ArrayList<>();
        buffer.retain();

        //模拟入站操作，此时就会执行decode方法
        channel.writeInbound(firBuf);//执行一次writeInbound实际上就会执行release()进行释放内存，由于这里切片所以为了避免释放，在此之前进行引用计数+1
        channel.writeInbound(secBuf);
    }

}