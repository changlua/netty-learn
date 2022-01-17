package com.changlu.server.handler.rpc;

import com.changlu.message.rpc.RpcResponseMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @ClassName RpcResponseMessageHandler
 * @Author ChangLu
 * @Date 2022/1/17 19:22
 * @Description 针对于处理RpcResponseMessage的handler
 */
@Slf4j
@ChannelHandler.Sharable
public class RpcResponseMessageHandler extends SimpleChannelInboundHandler<RpcResponseMessage> {

    public static Map<Integer, Promise> promiseMap = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponseMessage msg) throws Exception {
        log.debug("{}", msg);
        final Object returnValue = msg.getReturnValue();
        final Exception ex = msg.getExceptionValue();
        final Promise<Object> promise = promiseMap.remove(msg.getSequenceId());
        if (ex == null){
            promise.setSuccess(returnValue);
        }else{
            promise.setFailure(ex);
        }
    }
}