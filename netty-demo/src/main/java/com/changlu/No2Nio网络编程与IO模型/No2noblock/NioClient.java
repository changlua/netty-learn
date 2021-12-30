package com.changlu.No2Nio网络编程与IO模型.No2noblock;

import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 * @ClassName NioClient
 * @Author ChangLu
 * @Date 2021/12/18 14:55
 * @Description Nio客户端
 */
@Slf4j
public class NioClient {

    public static void main(String[] args) throws Exception{
        final SocketChannel sc = SocketChannel.open();
        log.debug("client is connecting ...");
        final boolean result = sc.connect(new InetSocketAddress("localhost", 8198));
        if (result){
            log.debug("client connect success!");
            //之后的一些请求内容通过debug调试发出！
            //示例（在Evaluate中执行）：sc.write(StandardCharsets.UTF_8.encode("hello!"))
        }else {
            log.debug("client is retrying...");
            sc.finishConnect();
        }
    }

}