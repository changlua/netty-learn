package com.changlu.No3Netty入门.No2Netty组件.future与promise;

import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * @ClassName NettyFutureTest
 * @Author ChangLu
 * @Date 2022/1/5 19:55
 * @Description Netty的Future测试：同步、异步方法
 */
@Slf4j
public class NettyFutureTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final NioEventLoopGroup group = new NioEventLoopGroup();
        //注意这个Future是netty中的Future
        final Future<Integer> future = group.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                log.debug("执行任务...");
                Thread.sleep(1000);
                return 666;
            }
        });
        log.debug("等待结果...");
        //方式一：同步取得结果(主线程阻塞获取)
//        log.debug("取值结果为：{}", future.get());
//        log.debug("取值结束！");

        //方式二：异步取得结果（执行任务线程来调用的回调方法）
        future.addListener(new GenericFutureListener<Future<? super Integer>>() {
            @Override
            public void operationComplete(Future<? super Integer> future) throws Exception {
                log.debug("取值结果为：{}", future.getNow());
            }
        });
        System.out.println("test...");
    }

}