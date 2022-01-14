package com.changlu.server.handler;

import com.changlu.message.QuitMemberRequestMessage;
import com.changlu.message.QuitMemberResponseMessage;
import com.changlu.server.session.SessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @ClassName QuitMemberRequestMessageHandler
 * @Author ChangLu
 * @Date 2022/1/14 21:48
 * @Description 指定用户下线执行器：【quitmember [username]】
 */
@ChannelHandler.Sharable
public class QuitMemberRequestMessageHandler extends SimpleChannelInboundHandler<QuitMemberRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, QuitMemberRequestMessage msg) throws Exception {
        String username = msg.getUsername();
        Channel channel = SessionFactory.getSession().getChannel(username);
        if (channel == null) {
            ctx.writeAndFlush(new QuitMemberResponseMessage(true, "该用户不在线！"));
        } else {
            //取消绑定以及强制关闭
            SessionFactory.getSession().unbind(channel);
            channel.close();
            ctx.writeAndFlush(new QuitMemberResponseMessage(true, "该用户已强制下线！"));
        }
    }
}