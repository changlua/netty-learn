package com.changlu.No2Nio网络编程与IO模型.No3selector.NO1AcceptRead;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

import static com.changlu.No1Nio基础组件入门学习.ByteBuffer.utils.ByteBufferUtil.debugAll;

/**
 * @ClassName NioServer
 * @Author ChangLu
 * @Date 2021/12/19 21:32
 * @Description 多路复用实践：使用selector实现单线程处理各类请求实践，含accept、read事件
 */
@Slf4j
public class NioServer {

    public static void main(String[] args) throws Exception{
        //1、创建selector，管理多个channel
        final Selector selector = Selector.open();
        final ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);//设置为非阻塞

        //2、建立selector和channel的联系（注册）
        // SelectionKey 就是将来事件发生后，通过它可以知道事件和哪个channel的事件
        final SelectionKey sscKey = ssc.register(selector, 0, null);//注册当前的socketchannel到selector上，参数为：选择器、感兴趣事件、附件
        log.debug("sscKey => {}",sscKey);
        // key只关注 accept事件
        sscKey.interestOps(SelectionKey.OP_ACCEPT);
        ssc.bind(new InetSocketAddress(8080));

        while (true) {
            //3、select方法，没有事件发生，线程阻塞，有事件线程才会恢复运行
            // select 在事件未处理时，他不会阻塞
            selector.select();//阻塞事件，若是选择器中的channel有感兴趣的事件发生，那么这里就不会进入阻塞状态
            //4、处理事件，selectKeys 内部包含了所有发生的事件
            final Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
            while (iter.hasNext()){
                SelectionKey key = iter.next();
                log.debug("SelectionKey：{}" + key);
                if (key.isAcceptable()) {
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    //注意：若是不执行accept()事件接收，那么selector会一直监听到事件情况！
                    SocketChannel sc = channel.accept();
                    log.debug("accept SocketChannel：{}" + sc);
                    sc.configureBlocking(false);
                    //初始化每个channel携带一个16字节的缓冲区用来进行读取
                    ByteBuffer initBuffer = ByteBuffer.allocate(16);
                    //注册该channel为读事件，每个channel携带一个ByteBuffer用来进行读取数据
                    SelectionKey scKey = sc.register(selector, 0, initBuffer);
                    scKey.interestOps(SelectionKey.OP_READ);
                    log.debug("注册read事件 => {}",scKey);
                }else if (key.isReadable()){
                    SocketChannel sc = (SocketChannel) key.channel();
//                    ByteBuffer buffer = ByteBuffer.allocate(16);
                    ByteBuffer buffer = (ByteBuffer) key.attachment();
                    try {
                        int size = sc.read(buffer);
                        log.debug("read字节数为：" + size);
                        //若是读取数量为-1，表示客户端正常执行close()关闭，取消订阅
                        if (size == -1){
                            key.cancel();
                        }else{
                            handle(buffer);
                            //若是初始read读到缓冲区的内容没有读完整，那么就会出现position=limit情况（因为找不到\n分隔符）
                            if (buffer.position() == buffer.limit()) {
                                buffer.flip();//切换到读模式
                                ByteBuffer newCapBuffer = ByteBuffer.allocate(buffer.capacity() * 2);
                                newCapBuffer.put(buffer);//重新进行读取，注意这里并没有切换到读模式，这是为了下次read()接上数据做准备
                                key.attach(newCapBuffer);
                                debugAll(newCapBuffer);
                            }
//                            buffer.flip();//切换为读模式
//                            System.out.println("读取内容：" + Charset.defaultCharset().decode(buffer).toString());
//                            debugAll(newCapBuffer);
                        }
                    }catch (IOException e){
                        e.printStackTrace();
                        //异常断开，直接取消对该key事件的关注
                        key.cancel();
                    }
                }
                //每次处理完一个事件都要直接将该事件进行移除，否则之后可能会依旧获取事件key，例如调用accept()出现null
                iter.remove();
//                key.cancel();//取消事件
            }
        }
    }

    /**
     * 处理黏包、半包情况：每次能够将\n结尾的内容读取到一个ByteBuffer，并测该ByteBuffer对象
     * @param buffer
     */
    private static void handle(ByteBuffer buffer) {
        buffer.flip();//切换到读状态
        for (int i = 0; i < buffer.limit(); i++) {
            //get(index)：仅仅只是获取当前索引内容，不会造成position移动
            if (buffer.get(i) == '\n') {
                int readLen = i - buffer.position() + 1;
                ByteBuffer temp = ByteBuffer.allocate(readLen);
                for (int j = 0; j < readLen; j++) {
                    temp.put(buffer.get());
                }
                debugAll(temp);
            }
        }
        buffer.compact();//切换写状态(压缩)：保留未读取的内容
    }

}