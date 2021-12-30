package com.changlu.No2Nio网络编程与IO模型.No2noblock;

import java.nio.ByteBuffer;

import static com.changlu.No1Nio基础组件入门学习.ByteBuffer.utils.ByteBufferUtil.debugAll;

/**
 * @ClassName Test
 * @Author ChangLu
 * @Date 2021/12/19 17:08
 * @Description TODO
 */
public class Test {

    public static void main(String[] args) {
        final ByteBuffer buffer = ByteBuffer.allocate(10);
        for (int i = 0; i < 20 ; i++) {
            buffer.put((byte)i);
        }
        debugAll(buffer);
    }

}