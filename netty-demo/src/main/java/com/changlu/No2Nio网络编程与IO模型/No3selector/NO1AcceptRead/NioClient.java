package com.changlu.No2Nio网络编程与IO模型.No3selector.NO1AcceptRead;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 * @ClassName NioClient
 * @Author ChangLu
 * @Date 2021/12/19 21:38
 * @Description NIO客户端
 */
public class NioClient {

    public static void main(String[] args) throws Exception{
        final SocketChannel sc = SocketChannel.open();
        final boolean result = sc.connect(new InetSocketAddress("localhost", 8080));
        if (result) {
            System.out.println("客户端连接成功");
        }
        //示例（在Evaluate中执行）：sc.write(StandardCharsets.UTF_8.encode("hello!"))
        System.in.read();
    }

}