package com.changlu.No2Nio网络编程与IO模型.No4AIO;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static com.changlu.No1Nio基础组件入门学习.ByteBuffer.utils.ByteBufferUtil.debugAll;

/**
 * @ClassName FileAIO
 * @Author ChangLu
 * @Date 2021/12/28 19:01
 * @Description 异步文件读取
 */
@Slf4j
public class FileAIO {

    public static void main(String[] args) {
        try (AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(Paths.get("data1.txt"), StandardOpenOption.READ)) {
            final ByteBuffer buffer = ByteBuffer.allocate(16);
            //1、首先执行
            log.info("read before...");
            //第三个参数是异步接口
            fileChannel.read(buffer, 0, null, new CompletionHandler<Integer, ByteBuffer>() {

                //3、实际读取完成之后其他线程执行
                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    log.info("read completed...");
                    buffer.flip();
                    debugAll(buffer);
                }

                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    exc.printStackTrace();
                }
            });
            //2、紧接着执行
            log.info("read end ...");
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}