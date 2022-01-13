package com.changlu.server.handler;

import com.changlu.message.ChatRequestMessage;
import com.changlu.message.ChatResponseMessage;
import com.changlu.server.session.SessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @ClassName ChatRequestMessageHandler
 * @Author ChangLu
 * @Date 2022/1/13 18:54
 * @Description 聊天请求对象处理器：针对于ChatRequestMessage
 */
@ChannelHandler.Sharable
public class ChatRequestMessageHandler extends SimpleChannelInboundHandler<ChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatRequestMessage msg) throws Exception {
        String to = msg.getTo();
        final Channel channel = SessionFactory.getSession().getChannel(to);
        //若是对方已下线，告知发送方消息
        if (channel == null){
            ctx.writeAndFlush(new ChatResponseMessage(msg.getFrom(), "对方用户不存在或已下线！"));
            return;
        }
        channel.writeAndFlush(new ChatResponseMessage(msg.getFrom(), msg.getContent()));
    }
}