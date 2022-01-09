package com.changlu.No4Netty进阶.No2协议设计与解析.No1常用协议示例.No3自定义协议示例.Test;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * @ClassName MessageCodec
 * @Author ChangLu
 * @Date 2022/1/9 15:27
 * @Description 实现ByteToMessageCodec：将ByteBuf转为指定的一个对象类型（可自己指定）
 */
@Slf4j
public class MessageCodec extends ByteToMessageCodec<Message> {

    /**
     * 出站前将封装好的Message对象写入到ByteBuf中
     * @param ctx
     * @param msg 封装好的消息对象
     * @param out 写入到消息对象中
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        //1、4个字节的魔数
        out.writeBytes(new byte[]{1,2,3,4});
        //2、1个字节版本号：1 表示版本1
        out.writeByte(1);
        //3、1个字节序列化算法：0 jdk；1 json
        out.writeByte(0);
        //4、1个字节指令类型：在Message对象中定义
        out.writeByte(msg.getMessageType());
        //5、4个字节：表示请求序号
        out.writeInt(msg.getSequenceId());
        //获取内容的字节数组（默认直接采用JDK对象序列化方式）
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(msg);
        final byte[] data = baos.toByteArray();
        //（额外）：为了满足2的N次方倍，要再加入一个字节凑满16个字节(除实际内容)
        // 仅仅目的是为了对齐填充
        out.writeByte(0xff);
        //6、4个字节length内容长度
        out.writeInt(data.length);
        //7、写入内容
        out.writeBytes(data);
    }

    /**
     * 进行解码：之前怎么封装的就怎么取
     * @param ctx
     * @param in
     * @param out
     * @throws Exception
     */
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