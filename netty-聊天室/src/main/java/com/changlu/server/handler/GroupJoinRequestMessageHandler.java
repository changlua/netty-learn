package com.changlu.server.handler;

import com.changlu.message.GroupJoinRequestMessage;
import com.changlu.message.GroupJoinResponseMessage;
import com.changlu.server.session.Group;
import com.changlu.server.session.GroupSessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @ClassName GroupJoinRequestMessageHandler
 * @Author ChangLu
 * @Date 2022/1/14 16:04
 * @Description 加入群聊处理器：【gjoin [group name]】
 */
@ChannelHandler.Sharable
public class GroupJoinRequestMessageHandler extends SimpleChannelInboundHandler<GroupJoinRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupJoinRequestMessage msg) throws Exception {
        String groupName = msg.getGroupName();
        String username = msg.getUsername();
        Group group = GroupSessionFactory.getGroupSession().joinMember(groupName, username);
        if (group != null){
            ctx.writeAndFlush(new GroupJoinResponseMessage(true, "群聊【"+ groupName +"】加入成功！"));
        }else{
            ctx.writeAndFlush(new GroupJoinResponseMessage(false, "当前无该群聊！"));
        }
    }
}