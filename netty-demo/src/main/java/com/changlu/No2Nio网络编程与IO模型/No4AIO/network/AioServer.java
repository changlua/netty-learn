package com.changlu.No2Nio网络编程与IO模型.No4AIO.network;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;

/**
 * @ClassName AioServer
 * @Author ChangLu
 * @Date 2021/12/28 19:16
 * @Description 网络异步：编写处理连接、读、写handler
 */
@Slf4j
public class AioServer {

    public static void main(String[] args){
        try (AsynchronousServerSocketChannel aioChannel = AsynchronousServerSocketChannel.open()) {
            aioChannel.bind(new InetSocketAddress(8080));
            //关联一个连接事件handler
            aioChannel.accept(null,new AcceptHandler(aioChannel));
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //关闭channel
    private static void closeChannel(AsynchronousSocketChannel sc) {
        try {
            System.out.printf("[%s] %s close\n", Thread.currentThread().getName(), sc.getRemoteAddress());
            sc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //读事件handler
    private static class ReadHandler implements CompletionHandler<Integer, ByteBuffer> {
        private final AsynchronousSocketChannel sc;

        public ReadHandler(AsynchronousSocketChannel sc) {
            this.sc = sc;
        }

        @Override
        public void completed(Integer result, ByteBuffer attachment) {
            try {
                if (result == -1) {
                    closeChannel(sc);
                    return;
                }
                System.out.printf("[%s] %s read\n", Thread.currentThread().getName(), sc.getRemoteAddress());
                attachment.flip();
                System.out.println(Charset.defaultCharset().decode(attachment));
                attachment.clear();
                // 处理完第一个 read 时，需要再次调用 read 方法来处理下一个 read 事件
                sc.read(attachment, attachment, this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void failed(Throwable exc, ByteBuffer attachment) {
            closeChannel(sc);
            exc.printStackTrace();
        }
    }

    //连接事件handler
    private static class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel, Object> {
        private final AsynchronousServerSocketChannel ssc;

        public AcceptHandler(AsynchronousServerSocketChannel ssc) {
            this.ssc = ssc;
        }

        @Override
        public void completed(AsynchronousSocketChannel sc, Object attachment) {
            try {
                System.out.printf("[%s] %s connected\n", Thread.currentThread().getName(), sc.getRemoteAddress());
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteBuffer buffer = ByteBuffer.allocate(16);
            // 读事件由 ReadHandler 处理
            sc.read(buffer, buffer, new ReadHandler(sc));
            // 写事件由 WriteHandler 处理（直接交由sc直接写出去）
            sc.write(Charset.defaultCharset().encode("server hello!"), ByteBuffer.allocate(16), new WriteHandler(sc));
            // 处理完第一个 accpet 时，需要再次调用 accept 方法来处理下一个 accept 事件
            ssc.accept(null, this);
        }

        @Override
        public void failed(Throwable exc, Object attachment) {
            exc.printStackTrace();
        }
    }

    //写事件handler
    private static class WriteHandler implements CompletionHandler<Integer, ByteBuffer>{

        private final AsynchronousSocketChannel ssc;

        public WriteHandler(AsynchronousSocketChannel ssc) {
            this.ssc = ssc;
        }

        @Override
        public void completed(Integer result, ByteBuffer attachment) {
            log.info("WriteHandle进行写事件...");
            //若是一次没有写完再次执行调用！
            if (attachment.hasRemaining()) {
                ssc.write(attachment,ByteBuffer.allocate(16),this);
            }
        }

        @Override
        public void failed(Throwable exc, ByteBuffer attachment) {

        }
    }


}