package com.changlu.No2Nio网络编程与IO模型.No3selector.NO3MultiThread;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 * @ClassName NIOClient
 * @Author ChangLu
 * @Date 2021/12/26 12:25
 * @Description NIO客户端
 */
public class NioClient {

    public static void main(String[] args) throws Exception{
        final SocketChannel sc = SocketChannel.open();
        final boolean result = sc.connect(new InetSocketAddress(8080));
        if (result) {
            System.out.println("连接成功");
        }else {
            sc.finishConnect();
        }
        //示例（在Evaluate中执行）：sc.write(StandardCharsets.UTF_8.encode("hello!"))
        System.in.read();
    }

}