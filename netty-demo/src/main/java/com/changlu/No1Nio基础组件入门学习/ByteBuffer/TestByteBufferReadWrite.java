package com.changlu.No1Nio基础组件入门学习.ByteBuffer;

import java.nio.ByteBuffer;
import static com.changlu.No1Nio基础组件入门学习.ByteBuffer.utils.ByteBufferUtil.debugAll;

/**
 * @ClassName TestByteBufferAllocate
 * @Author ChangLu
 * @Date 2021/12/16 20:41
 * @Description 测试ByteBuffer：put()、flip()、get()、compact()方法
 */
public class TestByteBufferReadWrite {

    /**
     * put()：写入字节，position+n
     * flip()：进入读模式，limit=position，position置空，
     * get()：读取position开始的字节，position+n
     * compact()：压缩内容，将未读取的字节向前移，position=limit，limit=总容量
     *
     * @param args
     */
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        //写入一个字节
        buffer.put((byte)0x61);
        debugAll(buffer);
        //进入读模式
        buffer.flip();
        debugAll(buffer);
        //读取一个字节
        System.out.println("读取字符：" + (char)buffer.get());
        debugAll(buffer);
        //测试compact()：切换写模式，写入三个字节,切换读模式，读取一个字节，进行压缩（保留未读字节向前移动，position指向未读字节之后）
        buffer.clear();
        buffer.put(new byte[]{0x62,0x63,0x64});
        buffer.flip();
        System.out.println("读取字符：" + (char)buffer.get());
        buffer.compact();
        debugAll(buffer);
    }
}
