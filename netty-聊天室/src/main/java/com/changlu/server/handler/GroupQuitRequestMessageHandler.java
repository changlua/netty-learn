package com.changlu.server.handler;

import com.changlu.message.GroupQuitRequestMessage;
import com.changlu.message.GroupQuitResponseMessage;
import com.changlu.server.session.Group;
import com.changlu.server.session.GroupSessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @ClassName GroupQuitRequestMessageHandler
 * @Author ChangLu
 * @Date 2022/1/14 16:09
 * @Description 退出群聊处理器：【gquit [group name]】
 */
@ChannelHandler.Sharable
public class GroupQuitRequestMessageHandler extends SimpleChannelInboundHandler<GroupQuitRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupQuitRequestMessage msg) throws Exception {
        final String username = msg.getUsername();
        final String groupName = msg.getGroupName();
        final Group group = GroupSessionFactory.getGroupSession().removeMember(groupName, username);
        if (group != null) {
            ctx.writeAndFlush(new GroupQuitResponseMessage(true, "退出群聊成功！"));
        }else{
            ctx.writeAndFlush(new GroupQuitResponseMessage(true, "该群聊不存在！"));
        }
    }
}