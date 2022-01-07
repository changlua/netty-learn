package com.changlu.No3Netty入门.No2Netty组件.ByteBuf.零拷贝.UnPool;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;

import static com.changlu.No3Netty入门.No2Netty组件.ByteBuf.ByteBufTest.log;

/**
 * @ClassName UnpooledTest
 * @Author ChangLu
 * @Date 2022/1/7 23:59
 * @Description Unpooled：非池化ByteBuf进行零拷贝的工具类
 */
public class UnpooledTest {

    public static void main(String[] args) {
        ByteBuf buf1 = ByteBufAllocator.DEFAULT.buffer(5);
        buf1.writeBytes(new byte[]{1, 2, 3, 4, 5});
        ByteBuf buf2 = ByteBufAllocator.DEFAULT.buffer(5);
        buf2.writeBytes(new byte[]{6, 7, 8, 9, 10});

        // 当包装 ByteBuf 个数超过一个时, 底层使用了 CompositeByteBuf
        ByteBuf buf3 = Unpooled.wrappedBuffer(buf1, buf2);
        buf3.setByte(0,6);
        log(buf1);
    }

}