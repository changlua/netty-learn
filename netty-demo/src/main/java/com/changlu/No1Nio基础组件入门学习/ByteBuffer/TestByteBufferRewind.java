package com.changlu.No1Nio基础组件入门学习.ByteBuffer;

import java.nio.ByteBuffer;
import static com.changlu.No1Nio基础组件入门学习.ByteBuffer.utils.ByteBufferUtil.debugAll;

/**
 * @ClassName TestByteBufferRewind
 * @Author ChangLu
 * @Date 2021/12/16 20:51
 * @Description 测试ByteBuffer的Rewind()方法
 */
public class TestByteBufferRewind {

    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put(new byte[]{0x61,0x62,0x63});
        buffer.flip();
        //测试get(index)
        System.out.println(buffer.get(2));
        debugAll(buffer);
        for (int i = 0; i < 3; i++) {
            if (i == 0)
                System.out.print("读取三个字节：");
            System.out.print((char)buffer.get() + " ");
        }
        System.out.println();
        debugAll(buffer);
        //rewind()（重复读取一遍）：position = 0
        buffer.rewind();
        System.out.println("执行rewind后：");
        debugAll(buffer);
        System.out.println("重新读取一遍：");
        for (int i = 0; i < 3; i++) {
            if (i == 0)
                System.out.print("读取三个字节：");
            System.out.print((char)buffer.get() + " ");
        }
    }

}