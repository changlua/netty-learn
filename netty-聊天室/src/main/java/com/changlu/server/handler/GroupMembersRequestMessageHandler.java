package com.changlu.server.handler;

import com.changlu.message.GroupMembersRequestMessage;
import com.changlu.message.GroupMembersResponseMessage;
import com.changlu.server.session.GroupSessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Set;

/**
 * @ClassName GroupMembersRequestMessageHandler
 * @Author ChangLu
 * @Date 2022/1/14 15:59
 * @Description 查看群成员信息：根据群名获取所有群成员信息。【gmembers [group name]】
 */
@ChannelHandler.Sharable
public class GroupMembersRequestMessageHandler extends SimpleChannelInboundHandler<GroupMembersRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupMembersRequestMessage msg) throws Exception {
        final String groupName = msg.getGroupName();
        final Set<String> members = GroupSessionFactory.getGroupSession().getMembers(groupName);
        ctx.channel().writeAndFlush(new GroupMembersResponseMessage(members));
    }
}