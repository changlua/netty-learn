package com.changlu.No1Nio基础组件入门学习.ByteBuffer;

import lombok.extern.slf4j.Slf4j;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @ClassName ByteBufferTest
 * @Author ChangLu
 * @Date 2021/12/16 18:55
 * @Description ByteBuffer基本使用
 */
@Slf4j
public class ByteBufferTest {
    public static void main(String[] args) {
        //Channel：①输入输出流取得，如FileChannel。②RandomAccessFile
        try (FileChannel channel = new FileInputStream("data.txt").getChannel()) {
            //准备缓冲区：开辟一块10字节的内存空间
            ByteBuffer byteBuffer = ByteBuffer.allocate(10);
            while (true){
                //从channel读取数据，向buffer写入
                int len = channel.read(byteBuffer);
                log.debug("已读取 {} 个字节", len);
                if (len == -1){
                    break;
                }
                byteBuffer.flip();//切换 buffer 读模式
                // 在buffer中是否还有剩余的内容
                while (byteBuffer.hasRemaining()) {
                    byte data = byteBuffer.get();
                    log.debug("当前读取的字节为：{}", (char)data);
                }
                byteBuffer.clear();// 切换 buffer 写模式
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}