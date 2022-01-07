package com.changlu.No3Netty入门.No2Netty组件.ByteBuf.零拷贝;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;

import java.nio.charset.Charset;

import static com.changlu.No3Netty入门.No2Netty组件.ByteBuf.ByteBufTest.log;

/**
 * @ClassName CompositeBufferTest
 * @Author ChangLu
 * @Date 2022/1/7 23:48
 * @Description CompositeBuffer：零拷贝之一，合并ByteBuf
 */
public class CompositeBufferTest {

    public static void main(String[] args) {
        final ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(20);
        buffer.writeCharSequence("changlu", Charset.defaultCharset());

        final ByteBuf buffer1 = ByteBufAllocator.DEFAULT.buffer(20);
        buffer1.writeCharSequence("liner", Charset.defaultCharset());

        //效率较低方案：直接通过writeBytes()写入字节方式写入
//        log(ByteBufAllocator.DEFAULT.buffer(20).writeBytes(buffer).writeBytes(buffer1));

        //零拷贝：合并两个Buffer到一个Buffer中，使用的共享内存
        final CompositeByteBuf comBuf = ByteBufAllocator.DEFAULT.compositeBuffer();
        //测试一：不设置true
//        comBuf.addComponents(buffer, buffer1);//若是不设置true，则不会自动调整读、写指针位置造成数据不会加进来
        //测试二：设置true
        comBuf.addComponents(true, buffer, buffer1);
        log(comBuf);
    }

}