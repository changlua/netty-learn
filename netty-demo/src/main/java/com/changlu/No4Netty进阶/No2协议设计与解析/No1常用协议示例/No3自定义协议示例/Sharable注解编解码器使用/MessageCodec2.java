package com.changlu.No4Netty进阶.No2协议设计与解析.No1常用协议示例.No3自定义协议示例.Sharable注解编解码器使用;

import com.changlu.No4Netty进阶.No2协议设计与解析.No1常用协议示例.No3自定义协议示例.Test.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * @ClassName MessageCodec2
 * @Author ChangLu
 * @Date 2022/1/9 23:30
 * @Description 可标注共享注解@ChannelHandler.Sharable的编解码器抽象类MessageToMessageCodec
 */
@Slf4j
@ChannelHandler.Sharable
public class MessageCodec2 extends MessageToMessageCodec<ByteBuf, Message> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
        final ByteBuf buffer = ctx.alloc().buffer();
        //1、4个字节的魔数
        buffer.writeBytes(new byte[]{1,2,3,4});
        //2、1个字节版本号：1 表示版本1
        buffer.writeByte(1);
        //3、1个字节序列化算法：0 jdk；1 json
        buffer.writeByte(0);
        //4、1个字节指令类型：在Message对象中定义
        buffer.writeByte(msg.getMessageType());
        //5、4个字节：表示请求序号
        buffer.writeInt(msg.getSequenceId());
        //获取内容的字节数组（默认直接采用JDK对象序列化方式）
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(msg);
        final byte[] data = baos.toByteArray();
        //（额外）：为了满足2的N次方倍，要再加入一个字节凑满16个字节(除实际内容)
        // 仅仅目的是为了对齐填充
        buffer.writeByte(0xff);
        //6、4个字节length内容长度
        buffer.writeInt(data.length);
        //7、写入内容
        buffer.writeBytes(data);
        out.add(buffer);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        final int magicNum = in.readInt();//魔术字
        final byte version = in.readByte();//版本号
        final byte serializerType = in.readByte();//序列号
        final byte messageType = in.readByte();//消息类型
        final int sequencedId = in.readInt();//请求序号
        in.readByte();//填充号
        final int length = in.readInt();//内容长度
        final byte[] data = new byte[length];
//        in.readBytes(data, 0, length);//内容（字节数组）
        in.readBytes(data, 0, in.readableBytes());//内容（字节数组）
        Message message = null;
        //进行jdk序列化（字节数组转为对象）
        if (serializerType == 0) {
            final ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
            message = (Message) ois.readObject();
        }
        out.add(message);
        log.debug("{}, {}, {}, {}, {}, {}", magicNum, version, messageType, sequencedId);
        log.debug("{}", message);
    }
}