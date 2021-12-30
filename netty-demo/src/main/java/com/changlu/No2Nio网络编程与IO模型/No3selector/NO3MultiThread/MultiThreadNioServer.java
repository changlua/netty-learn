package com.changlu.No2Nio网络编程与IO模型.No3selector.NO3MultiThread;

import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;

import static com.changlu.No1Nio基础组件入门学习.ByteBuffer.utils.ByteBufferUtil.debugAll;

/**
 * @ClassName MultThreadServer
 * @Author ChangLu
 * @Date 2021/12/24 21:03
 * @Description 多线程优化：①解决worker注册与select选择的阻塞问题。②多个worker(也就是多个selector)处理(非连接事件)。
 */
@Slf4j
public class MultiThreadNioServer {

    public static void main(String[] args) throws Exception{
        Thread.currentThread().setName("boss");
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        Selector boss = Selector.open();
        ssc.register(boss, SelectionKey.OP_ACCEPT, null);
        ssc.bind(new InetSocketAddress(8080));
        //1、创建固定数量的worker（由于Linux的bug问题，所以这里手动指定worker，否则通过代码来动态获取线程数量）
        Worker[] workers = new Worker[4];
//        Worker worker = new Worker("worker-0");
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new Worker("worker-" + i);
        }
        int count = 0;
        while (true) {
            //2、boss线程处理连接请求SocketChannel
            boss.select();
            Iterator<SelectionKey> iterator = boss.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isAcceptable()) {
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
                    log.debug("connected...{},sc {}",sc.getRemoteAddress(),sc);
                    //3、多个请求连接处理注册到不同的worker线程中去
                    //负载均衡依次平衡的注册到某个线程中去
                    workers[count++ % workers.length].register(sc);
                }
            }
        }

    }

    static class Worker implements Runnable{
        private Thread thread;//Worker线程
        private Selector selector;//一个Worker线程对应一个selector
        private String name;//线程名称
        private volatile boolean start = false;//还未初始化
        //用来临时存储SocketChannel用于进行注册
        private ConcurrentLinkedDeque<SocketChannel> queue = new ConcurrentLinkedDeque<>();

        public Worker(String name){
            this.name = name;
        }

        //初始化线程，
        public void register(SocketChannel sc) throws Exception {
            if (!start) {
                this.thread = new Thread(this,this.name);
                this.selector = Selector.open();
                this.thread.start();
                start = true;
            }
            //向队列添加SocketChannel，方便之后某个线程来进行同步方法执行注册操作
            queue.add(sc);
            this.selector.wakeup();//唤醒相关联selector的其他线程的select()阻塞
        }

        @Override
        public void run() {
            while (true) {
                try {
                    selector.select();
                    //***这部分注册代码是针对于手动调用wakeup()唤醒的过程！***
                    SocketChannel sc = queue.poll();
                    if (sc != null) {
                        log.debug("before register...{}",sc.getRemoteAddress());
                        sc.register(this.selector,SelectionKey.OP_READ,null);
                        log.debug("after register...{}",sc.getRemoteAddress());
                    }
                    //******************************************************
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        iterator.remove();
                        if (key.isReadable()) {
                            ByteBuffer buffer = ByteBuffer.allocate(16);
                            SocketChannel channel = (SocketChannel) key.channel();
                            channel.read(buffer);
                            buffer.flip();
                            log.info("key is {}",key);
                            debugAll(buffer);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }


}