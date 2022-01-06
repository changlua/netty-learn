package com.changlu.No3Netty入门.No2Netty组件.ByteBuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;
import static io.netty.util.internal.StringUtil.NEWLINE;

/**
 * @ClassName ByteBufTest
 * @Author ChangLu
 * @Date 2022/1/6 16:28
 * @Description ByteBuf案例：创建
 */
public class ByteBufTest {

    public static void main(String[] args) {
//        createByteBufDemo();
        seeByteBufClassDemo();
    }

    /**
     * ByteBuf创建：可进行自动扩容
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
     * 查看ByteBuf是否池化、采用的是直接内存或堆内存
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
    private static void log(ByteBuf buffer) {
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