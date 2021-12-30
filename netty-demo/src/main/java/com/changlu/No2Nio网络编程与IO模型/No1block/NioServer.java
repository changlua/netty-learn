package com.changlu.No2Nio网络编程与IO模型.No1block;

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
 * @ClassName Socket
 * @Author ChangLu
 * @Date 2021/12/18 14:28
 * @Description 阻塞NIO服务器
 */
@Slf4j
public class NioServer {

    private static List<SocketChannel> channels = new ArrayList<>();
    private static final ByteBuffer buffer = ByteBuffer.allocate(20);

    public static void main(String[] args)throws Exception{
        //1、创建服务器
        final ServerSocketChannel ssc = ServerSocketChannel.open();
        // 2. 绑定监听端口
        ssc.bind(new InetSocketAddress(8198));
        log.debug("server start ...");
        while (true) {
            log.debug("server accept ...");
            // 3. accept 建立与客户端连接， SocketChannel 用来与客户端之间通信（阻塞）
            final SocketChannel channel = ssc.accept();
            log.debug("channel => {}",channel);
            // 4. 添加连接至集合
            channels.add(channel);
            for (SocketChannel c : channels) {
                log.debug("before server read from {}...", c.getRemoteAddress());
                // 5. 接收客户端发送的数据（阻塞）
                c.read(buffer);
                //打印读取到的buffer内容
                debugAll(buffer);
                buffer.flip();//切换到读模式
                System.out.println("收到客户端：" + c + "，信息为：" + CharsetUtil.UTF_8.decode(buffer).toString());
                buffer.clear();//切换到写模式
                log.debug("end server read ...");
            }
        }

    }
}