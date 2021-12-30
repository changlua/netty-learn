package com.changlu.No2Nio网络编程与IO模型.No1block;

import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import static com.changlu.No1Nio基础组件入门学习.ByteBuffer.utils.ByteBufferUtil.debugAll;

/**
 * @ClassName NioServer2
 * @Author ChangLu
 * @Date 2021/12/18 15:12
 * @Description 改进NioServer：对于每一个连接的客户端单独开辟一个线程来处理（解决accept()、read()的阻塞问题）
 */

@Slf4j
public class NioServer2 {

    private static final ByteBuffer buffer = ByteBuffer.allocate(20);

    public static void main(String[] args)throws Exception{
        final ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.bind(new InetSocketAddress(8198));
        log.debug("server start ...");
        while (true) {
            log.debug("server accept ...");
            final SocketChannel channel = ssc.accept();
            log.debug("channel => {}",channel);
            //来了一个客户端连接就开辟一个线程来进行单独处理
            submitAccept(channel);
        }

    }

    public static void submitAccept(SocketChannel c){
        new Thread(()->{
            try {
                while (true) {
                    log.debug("before server read from {}...", c.getRemoteAddress());
                    c.read(buffer);
                    //打印读取到的buffer内容
                    debugAll(buffer);
                    buffer.flip();//切换到读模式
                    System.out.println("收到客户端：" + c + "，信息为：" + CharsetUtil.UTF_8.decode(buffer).toString());
                    buffer.clear();//切换到写模式
                    log.debug("end server read ...");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

}