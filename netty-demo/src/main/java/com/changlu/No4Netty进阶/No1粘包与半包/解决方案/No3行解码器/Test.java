package com.changlu.No4Netty进阶.No1粘包与半包.解决方案.No3行解码器;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;

import java.util.Arrays;
import java.util.Random;
/**
 * @ClassName Test
 * @Author ChangLu
 * @Date 2022/1/9 0:14
 * @Description LineBasedFrameDecoder（默认\n分隔符）、DelimiterBasedFrameDecoder（自定义分隔符）测试
 */
public class Test {

    public static void main(String[] args) {
        //测试一：LineBasedFrameDecoder
        testLineBasedFrameDecoder();
        //测试二：DelimiterBasedFrameDecoder
//        testDelimiterBasedFrameDecoder();
    }

    /**
     * 测试DelimiterBasedFrameDecoder界定符解码器：可自定义界定符（界定符为ByteBuf类型传入）
     */
    public static void testDelimiterBasedFrameDecoder(){
        char delimiter = '_';
        final ByteBuf buffer = getByteBufByDelimiter(delimiter);
        final ByteBuf delimiterByteBuf = ByteBufAllocator.DEFAULT.buffer().writeByte(delimiter);
        // DelimiterBasedFrameDecoder：参数一为检测的最大长度（若是超过最大长度还检测不到指定界定符直接抛异常），参数二为界定符
        final EmbeddedChannel channel = new EmbeddedChannel(new DelimiterBasedFrameDecoder(11, delimiterByteBuf), new LoggingHandler());
        // 模拟入站操作
        channel.writeInbound(buffer);
    }

    /**
     * 测试LineBasedFrameDecoder换行解码器：默认以\n作为分割。（）
     */
    public static void testLineBasedFrameDecoder(){
        final ByteBuf buffer = getByteBufByDelimiter('\n');
        // LineBasedFrameDecoder：换行解码器。这里构造器中填写的是检测最大长度，若是超过最大长度还检测不到直接抛异常
        final EmbeddedChannel channel = new EmbeddedChannel(new LineBasedFrameDecoder(11), new LoggingHandler());
        // 模拟入站操作
        channel.writeInbound(buffer);
    }

    private static ByteBuf getByteBufByDelimiter(char delimiter) {
        final ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        Random r = new Random();
        char c = 'a';
        for (int i = 0; i < 10; i++) {
            buffer.writeBytes(makeRandomBytes(r, c, delimiter));
            c++;
        }
        return buffer;
    }

    /**
     * 构造随机长度的字节数组末尾添加分隔符"\n"作为区分
     * @param r
     * @param c 随机字符
     * @param delimiter 界定符
     * @return
     */
    public static byte[] makeRandomBytes(Random r, char c, char delimiter){
        final int num = r.nextInt(10) + 1;
        final byte[] data = new byte[num + 1];
        //填充[0, num)区间
        Arrays.fill(data, 0, num, (byte) c);
        data[num] = (byte) delimiter;
        System.out.println(new String(data));
        return data;
    }

}