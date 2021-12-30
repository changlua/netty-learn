package com.changlu.No2Nio网络编程与IO模型.No4AIO.network;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import static com.changlu.No1Nio基础组件入门学习.ByteBuffer.utils.ByteBufferUtil.debugAll;

/**
 * @ClassName NioClient
 * @Author ChangLu
 * @Date 2021/12/28 19:29
 * @Description 客户端
 */
public class NioClient {
    public static void main(String[] args) throws Exception{
        final SocketChannel sc = SocketChannel.open();
        final boolean result = sc.connect(new InetSocketAddress("localhost", 8080));
        if (result) {
            System.out.println("客户端连接成功");
        }else{
            sc.finishConnect();
        }
        //接收数据
        final ByteBuffer buffer = ByteBuffer.allocate(16);
        sc.read(buffer);
        debugAll(buffer);
        //示例（在Evaluate中执行）：sc.write(StandardCharsets.UTF_8.encode("hello!"))
        System.in.read();
    }
}