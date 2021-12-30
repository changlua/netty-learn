package com.changlu.No2Nio网络编程与IO模型.No3selector.NO2Write;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @ClassName NioClient
 * @Author ChangLu
 * @Date 2021/12/19 21:38
 * @Description NIO客户端：接收服务端的大量写内容
 */
public class NioClient2 {

    public static void main(String[] args) throws Exception{
        final SocketChannel sc = SocketChannel.open();
        final boolean result = sc.connect(new InetSocketAddress("localhost", 8080));
        if (result) {
            System.out.println("客户端连接成功");
        }
        //当前的channel是阻塞的，那么每次read()都能够读取到内容，这里的话是接收一次大数据集的内容
        int count = 0;
        while (true) {
            ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
            int size = sc.read(buffer);
            if (size == 0) {
                break;
            }
            count += size;
            System.out.println("读取字节数：" + count);
        }
        //示例（在Evaluate中执行）：sc.write(StandardCharsets.UTF_8.encode("hello!"))
        System.in.read();
    }

}