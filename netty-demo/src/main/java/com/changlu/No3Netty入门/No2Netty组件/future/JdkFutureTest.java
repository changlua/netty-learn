package com.changlu.No3Netty入门.No2Netty组件.future;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * @ClassName JdkFutureTest
 * @Author ChangLu
 * @Date 2022/1/5 19:28
 * @Description JDK的Future测试：目的是线程间取值，其中get()方法是阻塞的。
 */
@Slf4j
public class JdkFutureTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final ExecutorService service = Executors.newFixedThreadPool(2);
        final Future<Integer> future = service.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                log.debug("执行计算...");
                Thread.sleep(1000);
                return 50;
            }
        });
        log.debug("等待计算结果...");
        //JDK的Future的get()是阻塞方法
        log.debug("取得计算结果为： {}", future.get());
        log.debug("运行结束！");
    }

}