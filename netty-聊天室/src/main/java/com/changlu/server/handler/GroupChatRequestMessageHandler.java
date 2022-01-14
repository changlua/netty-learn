package com.changlu.server.handler;

import com.changlu.message.GroupChatRequestMessage;
import com.changlu.message.GroupChatResponseMessage;
import com.changlu.server.session.GroupSessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;

/**
 * @ClassName GroupChatRequestMessageHandler
 * @Author ChangLu
 * @Date 2022/1/14 15:26
 * @Description 群组聊天：向群组发送一条消息。【gcreate [group name] [m1,m2,m3...]】
 */
@ChannelHandler.Sharable
public class GroupChatRequestMessageHandler extends SimpleChannelInboundHandler<GroupChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupChatRequestMessage msg) throws Exception {
        String groupName = msg.getGroupName();
        String content = msg.getContent();
        //根据群名取出所有的channel来进行发送数据
        List<Channel> membersChannel = GroupSessionFactory.getGroupSession().getMembersChannel(groupName);
        GroupChatResponseMessage responseMessage = new GroupChatResponseMessage(msg.getFrom(), content);
        responseMessage.setSuccess(true);
        for (Channel channel : membersChannel) {
            channel.writeAndFlush(responseMessage);
        }
    }
}