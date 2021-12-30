package com.changlu.No2Nio网络编程与IO模型.No2noblock;

import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import static com.changlu.No1Nio基础组件入门学习.ByteBuffer.utils.ByteBufferUtil.debugAll;

/**
 * @ClassName NioServer
 * @Author ChangLu
 * @Date 2021/12/18 14:28
 * @Description 非阻塞NIO服务器
 */
@Slf4j
public class NioServer {

    private static List<SocketChannel> channels = new ArrayList<>();
    private static final ByteBuffer buffer = ByteBuffer.allocate(20);

    public static void main(String[] args)throws Exception{
        final ServerSocketChannel ssc = ServerSocketChannel.open();
        //设置ServerSocketChannel为非阻塞：此时accept()就是非阻塞的，返回null说明没有连接
        ssc.configureBlocking(false);
        ssc.bind(new InetSocketAddress(8198));
        log.debug("server start ...");
        while (true) {
//            log.debug("server accept ...");
            final SocketChannel channel = ssc.accept();
            if (channel != null){
                //设置SocketChannel为非阻塞：此时read()就是非阻塞的，返回0则表示没有数据，>0表示有
                channel.configureBlocking(false);
                log.debug("channel => {}",channel);
                channels.add(channel);
            }
            for (SocketChannel c : channels) {
//                log.debug("before server read from {}...", c.getRemoteAddress());
                final int readSize = c.read(buffer);
                if (readSize > 0){
                    debugAll(buffer);
                    buffer.flip();
                    System.out.println("收到客户端：" + c + "，信息为：" + CharsetUtil.UTF_8.decode(buffer).toString());
                    buffer.clear();
                    log.debug("end server read ...");
                }
            }
        }

    }
}