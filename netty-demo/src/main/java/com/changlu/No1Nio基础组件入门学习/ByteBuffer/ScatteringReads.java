package com.changlu.No1Nio基础组件入门学习.ByteBuffer;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import static com.changlu.No1Nio基础组件入门学习.ByteBuffer.utils.ByteBufferUtil.debugAll;

/**
 * @ClassName ScatteringReads
 * @Author ChangLu
 * @Date 2021/12/17 17:32
 * @Description 分散读取文件内容至ByteBuffer
 */
public class ScatteringReads {

    public static void main(String[] args) {
        ByteBuffer b1 = ByteBuffer.allocate(7);
        ByteBuffer b2 = ByteBuffer.allocate(5);
        ByteBuffer b3 = ByteBuffer.allocate(3);

        try (FileChannel channel = new RandomAccessFile("data1.txt", "r").getChannel()) {
            //直接读取内容到各个ByteBuffer对象中
            channel.read(new ByteBuffer[]{b1,b2,b3});
            debugAll(b1);
            debugAll(b2);
            debugAll(b3);
        } catch (IOException e) {
        }

    }

}