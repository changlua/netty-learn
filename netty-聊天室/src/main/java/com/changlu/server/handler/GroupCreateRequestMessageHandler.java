package com.changlu.server.handler;

import com.changlu.message.GroupCreateRequestMessage;
import com.changlu.message.GroupCreateResponseMessage;
import com.changlu.server.session.*;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;
import java.util.Set;

/**
 * @ClassName GroupCreateRequestMessageHandler
 * @Author ChangLu
 * @Date 2022/1/14 14:46
 * @Description 新建群聊处理：创建群聊，并且拉入指定成员。【gsend [group name] [content]】
 */
@ChannelHandler.Sharable
public class GroupCreateRequestMessageHandler  extends SimpleChannelInboundHandler<GroupCreateRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupCreateRequestMessage msg) throws Exception {
        String groupName = msg.getGroupName();
        Set<String> members = msg.getMembers();
        //群组管理器
        final GroupSession groupSession = GroupSessionFactory.getGroupSession();
        Group group = groupSession.createGroup(groupName, members);
        //若是返回为null，表示原先没有，当前插入成功
        if (group == null){
            //响应一：创建成功，向原始客户端发送一条创建成功消息
            ctx.channel().writeAndFlush(new GroupCreateResponseMessage(true, "群组创建成功！"));
            List<Channel> membersChannel = groupSession.getMembersChannel(groupName);
            //响应二：向所有被拉入群聊的客户端发送一条被拉入群聊消息
            for (Channel channel : membersChannel) {
                channel.writeAndFlush(new GroupCreateResponseMessage(true, "你已经被拉取群聊：" + groupName));
            }
        } else {
            //响应三：创建失败，向源客户端发送提示信息
            ctx.channel().writeAndFlush(new GroupCreateResponseMessage(false, "群组已存在！"));
        }

    }
}