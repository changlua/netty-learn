package com.changlu.No1Nio基础组件入门学习.ByteBuffer;

import io.netty.util.CharsetUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @ClassName GatheringWrites
 * @Author ChangLu
 * @Date 2021/12/17 17:37
 * @Description 分散写：将多个ByteBuffer中内容依次写入到一个文件中
 */
public class GatheringWrites {

    public static void main(String[] args) {
        ByteBuffer b1 = CharsetUtil.UTF_8.encode("changlu");
        ByteBuffer b2 = CharsetUtil.UTF_8.encode("hennuli");

        try (FileChannel channel = new RandomAccessFile("data3.txt", "rw").getChannel()) {
            //将多个ByteBuffer中存储的内容依次写入到文件中
            channel.write(new ByteBuffer[]{b1,b2});
        } catch (IOException e) {
        }
    }

}