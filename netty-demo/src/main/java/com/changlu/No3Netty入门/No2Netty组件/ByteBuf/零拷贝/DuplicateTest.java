package com.changlu.No3Netty入门.No2Netty组件.ByteBuf.零拷贝;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import java.nio.charset.Charset;

import static com.changlu.No3Netty入门.No2Netty组件.ByteBuf.ByteBufTest.log;

/**
 * @ClassName DuplicateTest
 * @Author ChangLu
 * @Date 2022/1/7 23:32
 * @Description Duplicate：整块零拷贝
 */
public class DuplicateTest {

    public static void main(String[] args) {
        final ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(20);
        buffer.writeCharSequence("changlu", Charset.defaultCharset());
        final ByteBuf dupBuf = buffer.duplicate();
        //对整块进行零拷贝的进行修改
        dupBuf.setByte(0,1);
        log(buffer);//测试源ByteBuf受到影响
    }

}