package com.changlu;


import com.changlu.config.Config;
import com.changlu.message.LoginRequestMessage;
import com.changlu.protocol.serialize.Serializer;
import com.changlu.protocol.serialize.SerializerAlgorithm;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;
import static io.netty.util.internal.StringUtil.NEWLINE;

/**
 * @ClassName com.changlu.TestSerializer
 * @Author ChangLu
 * @Date 2022/1/15 14:40
 * @Description 序列化实现类测试
 */
public class TestSerializer {

    private static Serializer algorithm;

    public static void main(String[] args) {
//        testJDK();
        testJSON();
//        testReadConfigAlgorithm();
    }

    /**
     * 3、测试读取配置文件中指定的序列化算法
     */
    private static void testReadConfigAlgorithm() {
        final int num = Config.getSerializerAlgorithm().ordinal();
        System.out.println(num);
        System.out.println(SerializerAlgorithm.values()[num]);
    }

    /**
     * 2、测试JSON序列化
     */
    private static void testJSON() {
        algorithm = SerializerAlgorithm.Json;
        testSerializeAlgorithm();
    }

    /**
     * 1、测试JDK序列化
     */
    private static void testJDK() {
        algorithm = SerializerAlgorithm.Java;
        testSerializeAlgorithm();
    }

    /**
     * 序列化、反序列化通用测试代码
     */
    private static void testSerializeAlgorithm() {
        final LoginRequestMessage message = new LoginRequestMessage("changlu", "123456");
        System.out.println("序列化后结果");
        //序列化调用
        final byte[] bytes = algorithm.serialize(message);
        log(ByteBufAllocator.DEFAULT.buffer().writeBytes(bytes));

        System.out.println("反序列化后结果");
        //进行反序列化
        LoginRequestMessage loginRequestMessage = algorithm.deserialize(LoginRequestMessage.class, bytes);
        System.out.println(loginRequestMessage);
    }


    /**
     * 工具类：用于方便查看ByteBuf中的具体数据信息
     * @param buffer
     */
    public static void log(ByteBuf buffer) {
        int length = buffer.readableBytes();
        int rows = length / 16 + (length % 15 == 0 ? 0 : 1) + 4;
        StringBuilder buf = new StringBuilder(rows * 80 * 2)
                .append("read index:").append(buffer.readerIndex())
                .append(" write index:").append(buffer.writerIndex())
                .append(" capacity:").append(buffer.capacity())
                .append(NEWLINE);
        appendPrettyHexDump(buf, buffer);
        System.out.println(buf.toString());
    }


}