package com.changlu.No3Netty入门.No2Netty组件.ByteBuf.零拷贝;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import java.nio.charset.Charset;

import static com.changlu.No3Netty入门.No2Netty组件.ByteBuf.ByteBufTest.log;

/**
 * @ClassName CopyTest
 * @Author ChangLu
 * @Date 2022/1/7 23:37
 * @Description Copy：整个ByteBuf进行深拷贝
 */
public class CopyTest {

    public static void main(String[] args) {
        final ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(20);
        buffer.writeCharSequence("changlu", Charset.defaultCharset());
        //进行深拷贝
        final ByteBuf copyBuf = buffer.copy();
        copyBuf.setByte(0,1);
        copyBuf.writeByte(6);
        //测试源buffer
        log(buffer);
        //测试深拷贝得到buffer
        log(copyBuf);
    }

}