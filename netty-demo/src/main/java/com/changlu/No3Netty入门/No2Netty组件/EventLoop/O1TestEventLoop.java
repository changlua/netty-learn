package com.changlu.No3Netty入门.No2Netty组件.EventLoop;

import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName TestEventLoop
 * @Author ChangLu
 * @Date 2022/1/2 21:41
 * @Description 测试EventLoop处理普通、定时任务
 */
@Slf4j
public class O1TestEventLoop {

    public static void main(String[] args) {
        //1、创建事件循环组
        NioEventLoopGroup group = new NioEventLoopGroup(2);// io事件，普通任务，定时任务
//        DefaultEventLoopGroup group1 = new DefaultEventLoopGroup();// 普通任务，定时任务
//        System.out.println(NettyRuntime.availableProcessors());//打印本机的CPU核心数量，8核
        //2、获取下一个事件循环对象（可不断循环获取）
        System.out.println(group.next());
        System.out.println(group.next());
        System.out.println(group.next());
        System.out.println(group.next());

        //3、执行普通任务
//        group.next().submit(()->{  //或者使用execute()方法提交都是可以的
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            log.debug("ok!");
//        });

        //或3、执行定时任务
        group.next().scheduleAtFixedRate(()->{
            log.debug("test");
        }, 0 , 1, TimeUnit.SECONDS);

        log.debug("main!");

    }

}