package com.changlu.No1Nio基础组件入门学习.ByteBuffer;
import java.nio.ByteBuffer;

import static com.changlu.No1Nio基础组件入门学习.ByteBuffer.utils.ByteBufferUtil.debugAll;

/**
 * @ClassName TestByteBufferMark_Reset
 * @Author ChangLu
 * @Date 2021/12/16 21:03
 * @Description 测试ByteBuffer的方法：mark()、reset()
 */
public class TestByteBufferMark_Reset {

    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.put(new byte[]{0x62,0x63,0x64});
        buffer.flip();
        System.out.println((char)buffer.get());
        buffer.mark();//mark进行标记该位置：mark=position
        System.out.println((char)buffer.get());
        System.out.println((char)buffer.get());
        debugAll(buffer);
        buffer.reset();//恢复到原标记位置：position=mark
        debugAll(buffer);
        //重复读两次
        System.out.println("恢复标记后，重新读取：");
        System.out.println((char)buffer.get());
        System.out.println((char)buffer.get());
    }
}