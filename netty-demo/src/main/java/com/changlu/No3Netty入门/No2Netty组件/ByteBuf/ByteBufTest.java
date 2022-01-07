package com.changlu.No3Netty入门.No2Netty组件.ByteBuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;
import static io.netty.util.internal.StringUtil.NEWLINE;

/**
 * @ClassName ByteBufTest
 * @Author ChangLu
 * @Date 2022/1/6 16:28
 * @Description ByteBuf案例：创建
 */
@Slf4j
public class ByteBufTest {

    public static void main(String[] args) {
//        createByteBufDemo();
//        seeByteBufClassDemo();
//        writeToByteBufDemo();
        readByteBufDemo();
    }

    /**
     * 04、测试ByteBuf的读取：包含重复读取某个字节
     */
    public static void readByteBufDemo(){
        final ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(20);
        buffer.writeBytes("123456789".getBytes());//写入字节
        System.out.println(buffer.readByte());
        System.out.println(buffer.readByte());
        System.out.println(buffer.readByte());
        System.out.println(buffer.readByte());
        buffer.markReaderIndex();//可标记读索引以及写索引
        buffer.readBytes(4);
        buffer.resetReaderIndex();
        log.debug("读取读索引的字节");
        System.out.println(buffer.readByte());
    }

    /**
     * 03、测试ByteBuf的写入与扩容
     */
    public static void writeToByteBufDemo(){
        final ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(20);
        buffer.writeBytes("c".getBytes());//写入字节
        final StringBuilder builder = new StringBuilder("hang");
        buffer.writeCharSequence(builder, Charset.defaultCharset());//写入stringbuilder
        buffer.writeCharSequence("lu", Charset.defaultCharset());//写入字符串
        log(buffer);
        //测试扩容
        buffer.writeCharSequence(",helloworld", Charset.defaultCharset());
        log(buffer);
    }

    /**
     * 01、ByteBuf创建：可进行自动扩容
     */
    public static void createByteBufDemo(){
        final ByteBuf bytebuf = ByteBufAllocator.DEFAULT.buffer(20);
//        System.out.println(bytebuf);//toString()的一些内容展示有限：PooledUnsafeDirectByteBuf(ridx: 0, widx: 0, cap: 20)
        log(bytebuf);
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 50; i++) {
            builder.append("a");
        }
        //向ByteBuffer中写入数据
        bytebuf.writeBytes(builder.toString().getBytes());
//        System.out.println(bytebuf);
        log(bytebuf);
    }

    /**
     * 02、查看ByteBuf是否池化、采用的是直接内存或堆内存
     */
    public static void seeByteBufClassDemo(){
        //buffer()：默认是直接内存
        System.out.println(ByteBufAllocator.DEFAULT.buffer().getClass());
        //directBuffer()：直接内存
        System.out.println(ByteBufAllocator.DEFAULT.directBuffer().getClass());
        //heapBuffer()：堆内存
        System.out.println(ByteBufAllocator.DEFAULT.heapBuffer().getClass());
    }

    /**
     * 工具类：用于方便查看ByteBuf中的具体数据信息
     * @param buffer
     */
    public static void log(ByteBuf buffer) {
        int length = buffer.readableBytes();
        int rows = length / 16 + (length % 15 == 0 ? 0 : 1) + 4;
        StringBuilder buf = new StringBuilder(rows * 80 * 2)
                .append("read index:").append(buffer.readerIndex())
                .append(" write index:").append(buffer.writerIndex())
                .append(" capacity:").append(buffer.capacity())
                .append(NEWLINE);
        appendPrettyHexDump(buf, buffer);
        System.out.println(buf.toString());
    }


}