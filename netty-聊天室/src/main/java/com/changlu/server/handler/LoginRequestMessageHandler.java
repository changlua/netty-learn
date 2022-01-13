package com.changlu.server.handler;

import com.changlu.message.LoginRequestMessage;
import com.changlu.message.LoginResponseMessage;
import com.changlu.server.service.UserServiceFactory;
import com.changlu.server.session.SessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @ClassName LoginRequestMessageHandler
 * @Author ChangLu
 * @Date 2022/1/13 18:50
 * @Description 登陆请求对象处理器：LoginRequestMessage
 */
@ChannelHandler.Sharable
public class LoginRequestMessageHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage msg) throws Exception {
        String username = msg.getUsername();
        String password = msg.getPassword();
        boolean login = UserServiceFactory.getUserService().login(username, password);
        LoginResponseMessage message;
        if (login) {
            message = new LoginResponseMessage(true, "登陆成功！");
            SessionFactory.getSession().bind(ctx.channel(), username);
        } else {
            message = new LoginResponseMessage(false, "登陆失败！");
        }
        ctx.writeAndFlush(message);
    }
}