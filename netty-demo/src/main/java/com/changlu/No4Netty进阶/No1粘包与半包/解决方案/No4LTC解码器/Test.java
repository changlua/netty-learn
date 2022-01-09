package com.changlu.No4Netty进阶.No1粘包与半包.解决方案.No4LTC解码器;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import static com.changlu.No3Netty入门.No2Netty组件.ByteBuf.ByteBufTest.log;

/**
 * @ClassName Test
 * @Author ChangLu
 * @Date 2022/1/9 8:54
 * @Description LengthFieldBasedFrameDecoder测试：基于长度字段帧解码器，指定内容长度
 */
public class Test {
    public static void main(String[] args) {
//        test01();
        test02();
    }


    /**
     * demo2：读取（head1 length head2 msg）组合中的msg
     */
    public static void test02(){
        LengthFieldBasedFrameDecoder decoder = new LengthFieldBasedFrameDecoder(1024, 1, 4, 1, 6);
        EmbeddedChannel channel = new EmbeddedChannel(decoder, new LoggingHandler(LogLevel.DEBUG));
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        //插入三条数据
        writeMsg2(buffer, "changlu");
        writeMsg2(buffer, "liner");
        writeMsg2(buffer, "love");
        System.out.println("\n---------------------------发送数据-----------------------------");
        log(buffer);
        System.out.println("---------------------------发送数据-----------------------------\n");
        channel.writeInbound(buffer);
    }

    /**
     * demo1：读取（length msg）组合中的msg
     */
    public static void test01(){
        //构造参数：1024 0 4 0 4，参数一指的是检测最大容量，之后则是基础四个配置参数
        LengthFieldBasedFrameDecoder decoder = new LengthFieldBasedFrameDecoder(1024, 0, 4, 0, 4);
        EmbeddedChannel channel = new EmbeddedChannel(decoder, new LoggingHandler(LogLevel.DEBUG));
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        //插入两条数据
        writeMsg1(buffer, "changlu");
        writeMsg1(buffer, "liner");
        System.out.println("\n---------------------------发送数据-----------------------------");
        log(buffer);
        System.out.println("---------------------------发送数据-----------------------------\n");
        channel.writeInbound(buffer);
    }

    /**
     * 内容组成：length msg
     * @param buffer
     * @param msg
     */
    private static void writeMsg1(ByteBuf buffer, String msg) {
        final int length = msg.length();
        buffer.writeInt(length);//实际长度：注意这里writeint写的是int类型，占用四个字节
        buffer.writeBytes(msg.getBytes());//实际内容
    }

    /**
     * 内容组成 head1 length head2 msg
     * @param buffer
     * @param msg
     */
    private static void writeMsg2(ByteBuf buffer, String msg) {
        final int length = msg.length();
        int head1 = 1;
        int head2 = 2;
        //写入四个内容：head1 length head2 msg，最终要读取到msg信息
        buffer.writeByte(head1);
        buffer.writeInt(length);//实际长度：注意这里writeint写的是int类型，占用四个字节
        buffer.writeByte(head2);
        buffer.writeBytes(msg.getBytes());//实际内容
    }


}