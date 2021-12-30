package com.changlu.No2Nio网络编程与IO模型.No3selector.NO2Write;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * @ClassName NioServer2
 * @Author ChangLu
 * @Date 2021/12/20 19:51
 * @Description 多路复用实践：使用selector实现单线程处理各类请求实践，单独来处理写事件
 */
public class NioServer2 {

    public static void main(String[] args) throws Exception{
        Selector selector = Selector.open();
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.bind(new InetSocketAddress(8080));
        ssc.configureBlocking(false);
        //将服务器channel设置为对accept()感兴趣
        ssc.register(selector, SelectionKey.OP_ACCEPT,null);

        while (true) {
            selector.select();
            final Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                final SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isAcceptable()) {
                    ServerSocketChannel sc = (ServerSocketChannel) key.channel();
                    SocketChannel channel = sc.accept();
                    channel.configureBlocking(false);//若是不设置非阻塞的话，在进行write写时就会一直阻塞等待到写完成位置！
                    SelectionKey selKey = channel.register(selector, SelectionKey.OP_READ);
                    //1、向客户端发送大量数据
                    StringBuilder str = new StringBuilder();
                    for (int i = 0; i < 30000000; i++) {
                        str.append("a");
                    }
                    ByteBuffer buffer = Charset.defaultCharset().encode(str.toString());

                    //2、返回值代表实际写入的字节数
                    //开始进行写操作：若是一次不能够直接写完所有内容，那么就将其添加至写事件
                    int writeSize = channel.write(buffer);
                    System.out.println(writeSize);

                    //3、判断是否有剩余内容
                    if (buffer.hasRemaining()) {
                        //4、关注可写事件
                        //注意：不能够直接设置写事件，需要在原有基础上添加指定的感兴趣事件
                        selKey.interestOps(selKey.interestOps() + SelectionKey.OP_WRITE);
                        selKey.attach(buffer);
                    }
                }else if (key.isWritable()) {  // 5、若是网络缓冲区又有空间能够写入，则会触发该事件
                    ByteBuffer buffer = (ByteBuffer) key.attachment();
                    SocketChannel channel = (SocketChannel) key.channel();
                    //6、继续通过通道向客户端写入内容
                    int writeSize = channel.write(buffer);
                    System.out.println(writeSize);
                    //7、最终的清理操作
                    //  若是当前已经写完所有内容了，那么就取消关注该key的写事件，并不携带附件内容
                    if (!buffer.hasRemaining()) {
                        key.interestOps(key.interestOps() - SelectionKey.OP_WRITE);
                        key.attach(null);
                    }
                }

            }
        }

    }

}