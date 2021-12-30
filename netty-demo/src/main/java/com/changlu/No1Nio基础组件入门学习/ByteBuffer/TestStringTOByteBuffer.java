package com.changlu.No1Nio基础组件入门学习.ByteBuffer;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static com.changlu.No1Nio基础组件入门学习.ByteBuffer.utils.ByteBufferUtil.debugAll;

/**
 * @ClassName TestStringTOByteBuffer
 * @Author ChangLu
 * @Date 2021/12/16 21:13
 * @Description String与ByteBuffer互转
 */
public class TestStringTOByteBuffer {

    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        //字符串转为bytebuffer
        //方式一：put()时直接字符串转为byte[]写入
        buffer.put("changlu".getBytes());
        debugAll(buffer);

        //方式二：StandardCharsets.UTF_8进行编码创建对象。
        //内部过程：按需创建空间，依次填入内容后，执行写模式
        ByteBuffer buffer2 = StandardCharsets.UTF_8.encode("changlu");
        debugAll(buffer2);

        //方式三：ByteBuffer.wrap()
        ByteBuffer buffer3 = ByteBuffer.wrap("changlu".getBytes());
        debugAll(buffer3);

        //ByteBuffer转字符串
        //方式一：StandardCharsets.UTF_8.decode，原理就是从position位置开始读取已有的内容
        System.out.println(StandardCharsets.UTF_8.decode(buffer2).toString());
        //注意：对于上面方式一put()进入由于没有转换为读状态所以需要先进行转换状态，也就是要position=0
        buffer.flip();
        System.out.println(StandardCharsets.UTF_8.decode(buffer).toString());
    }

}