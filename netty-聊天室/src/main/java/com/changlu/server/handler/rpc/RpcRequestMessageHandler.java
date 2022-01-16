package com.changlu.server.handler.rpc;

import com.changlu.message.rpc.RpcRequestMessage;
import com.changlu.message.rpc.RpcResponseMessage;
import com.changlu.server.service.rpc.RpcServiceImpl;
import com.changlu.server.service.rpc.ServicesFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * @ClassName RpcRequestMessageHandler
 * @Author ChangLu
 * @Date 2022/1/16 22:05
 * @Description TODO
 */
@Slf4j
@ChannelHandler.Sharable
public class RpcRequestMessageHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage message) throws Exception {
        final Class<?> clazz = Class.forName(message.getInterfaceName());
        final Method method = clazz.getMethod(message.getMethodName(), message.getParameterTypes());
        final Object returnVal = method.invoke(ServicesFactory.getServiceImpl(clazz), message.getParameterValue());
        ctx.channel().writeAndFlush(new RpcResponseMessage(returnVal, null));
    }

    public static void main(String[] args) throws Exception{
        final RpcRequestMessage message = new RpcRequestMessage(
                1,
                "com.changlu.server.service.rpc.RpcService",
                "sayHello",
                String.class,
                new Class[]{String.class},
                new Object[]{"changlu"}
        );
        final Class<?> clazz = Class.forName(message.getInterfaceName());
        final Method method = clazz.getMethod(message.getMethodName(), message.getParameterTypes());
        final Object returnVal = method.invoke(ServicesFactory.getServiceImpl(clazz), message.getParameterValue());
        System.out.println(returnVal);
        //ServicesFactory.getServiceImpl测试：根据配置文件来获取指定实现子类的实例！
//        final Object service = ServicesFactory.getServiceImpl(Class.forName(message.getInterfaceName()));
//        RpcServiceImpl rpcService = (RpcServiceImpl)service;
//        System.out.println(rpcService.sayHello("changlu"));
    }

}