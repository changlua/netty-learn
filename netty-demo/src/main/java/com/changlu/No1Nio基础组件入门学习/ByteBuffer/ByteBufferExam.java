package com.changlu.No1Nio基础组件入门学习.ByteBuffer;

import java.nio.ByteBuffer;

import static com.changlu.No1Nio基础组件入门学习.ByteBuffer.utils.ByteBufferUtil.debugAll;

/**
 * @ClassName ByteBufferExam
 * @Author ChangLu
 * @Date 2021/12/17 17:58
 * @Description 黏包、半包解析（底层实现）
 */
public class ByteBufferExam {

    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(256);
        //黏包情况：
        buffer.put("I am changlu!\nhello,xiaoli\nha".getBytes());
        handle(buffer);
        //半包情况：
        buffer.put(",are you ok?\n".getBytes());
        handle(buffer);
    }

    /**
     * 处理黏包、半包情况：每次能够将\n结尾的内容读取到一个ByteBuffer，并测该ByteBuffer对象
     * @param buffer
     */
    private static void handle(ByteBuffer buffer) {
        buffer.flip();//切换到读状态
        for (int i = 0; i < buffer.limit(); i++) {
            //get(index)：仅仅只是获取当前索引内容，不会造成position移动
            if (buffer.get(i) == '\n') {
                int readLen = i - buffer.position() + 1;
                ByteBuffer temp = ByteBuffer.allocate(readLen);
                for (int j = 0; j < readLen; j++) {
                    temp.put(buffer.get());
                }
                debugAll(temp);
            }
        }
        buffer.compact();//切换写状态(压缩)：保留未读取的内容
    }

}