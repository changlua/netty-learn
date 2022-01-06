package com.changlu.No3Netty入门.No2Netty组件.future与promise;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;

/**
 * @ClassName NettyPromiseTest
 * @Author ChangLu
 * @Date 2022/1/6 13:25
 * @Description Netty中的Promise使用：对某个业务处理结果设置成功或失败
 */
@Slf4j
public class NettyPromiseTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final EventLoop eventLoop = new NioEventLoopGroup().next();
        final DefaultPromise<Integer> promise = new DefaultPromise<>(eventLoop);
        new Thread(()->{
            log.debug("开始执行任务...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //1、处理结果设置成功！
//            promise.setSuccess(100);
            //2、处理结果设置失败！
            try {
                int i = 10/0;
            }catch (Exception e){
//                e.printStackTrace();
                //在异常中设置失败结果
                promise.setFailure(e);
            }
        }).start();

        log.debug("等待任务结果...");
        //get()方法是一个阻塞方法。若是任务成功会直接返回值；若是任务失败会抛出异常
        log.debug("等待得到的结果为：{}",promise.get());
        log.debug("test...");
    }

}