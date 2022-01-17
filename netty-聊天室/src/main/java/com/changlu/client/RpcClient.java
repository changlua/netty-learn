package com.changlu.client;

import com.changlu.message.rpc.RpcRequestMessage;
import com.changlu.protocol.MessageCodecSharable;
import com.changlu.protocol.ProcotolFrameDecoder;
import com.changlu.server.handler.rpc.RpcResponseMessageHandler;
import com.changlu.server.service.rpc.RpcService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName RpcClient
 * @Author ChangLu
 * @Date 2022/1/16 21:45
 * @Description RPC远程调用客户端
 */
@Slf4j
public class RpcClient {

    private static volatile Channel channel = null;
    private static Object lock = new Object();
    //并发计数器
    private static AtomicInteger seqId = new AtomicInteger(0);


    public static void main(String[] args) {
        final RpcService proxyService = getProxyService(RpcService.class);
        //测试
        System.out.println(proxyService.sayHello("changlu"));
//        System.out.println(proxyService.sayHello("liner"));
//        System.out.println(proxyService.sayHello("world!"));
    }

    private static <T> T getProxyService(Class<T> clazz) {
        ClassLoader classLoader = clazz.getClassLoader();
        Class[] classes = {clazz};
        return (T)Proxy.newProxyInstance(classLoader, classes, (proxy, method, args)->{
            RpcRequestMessage message = new RpcRequestMessage(
                    seqId.getAndIncrement(),
                    clazz.getName(),  //示例：com.changlu.server.service.rpc.RpcService
                    method.getName(), //sayHello
                    method.getReturnType(),  //String.class
                    method.getParameterTypes(), //new Class[]{String.class}
                    args  //new Object[]{"changlu"}
            );
            //用于其他线程与当前主线程进行通信
            final DefaultPromise<Object> promise = new DefaultPromise<>(getChannel().eventLoop());
            RpcResponseMessageHandler.promiseMap.put(message.getSequenceId(), promise);
            //远程调用（获取channel方式是通过单例模式来获取）
            getChannel().writeAndFlush(message);
            promise.await();//阻塞等待
            if (promise.isSuccess()) {
                return promise.getNow();
            }else{
                throw new RuntimeException((Exception)promise.getNow());
            }
        });
    }

    /**
     * 单例(懒汉，双重检测锁)：获取channel
     * @return
     */
    public static Channel getChannel(){
        if (channel != null) {
            return channel;
        }
        synchronized (lock) {
            if (channel != null){
                return channel;
            }
            initChannel();//初始化channel
            return channel;
        }
    }

    /**
     * 初始化channel
     */
    private static void initChannel() {
        MessageCodecSharable messageCodec = new MessageCodecSharable();
        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        RpcResponseMessageHandler responseMessageHandler = new RpcResponseMessageHandler();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap()
                    .group(worker)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
//                            ch.pipeline().addLast(new StringEncoder());
                            ch.pipeline().addLast(new ProcotolFrameDecoder());
                            ch.pipeline().addLast(loggingHandler);
                            ch.pipeline().addLast(messageCodec);
                            ch.pipeline().addLast(responseMessageHandler);
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    //初始测试
//                                    final RpcRequestMessage message = new RpcRequestMessage(
//                                            1,
//                                            "com.changlu.server.service.rpc.RpcService",
//                                            "sayHello",
//                                            String.class,
//                                            new Class[]{String.class},
//                                            new Object[]{"changlu"}
//                                    );
//                                    ctx.channel().writeAndFlush(message);
                                }
                            });
                        }
                    });
//        Channel channel = null;
        try {
            channel = bootstrap.connect("127.0.0.1", 8080).sync().channel();
            log.debug("客户端连接成功！");
            channel.closeFuture().addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    log.debug("客户端关闭连接！");
                    worker.shutdownGracefully();
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}