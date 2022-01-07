package com.changlu.No3Netty入门.No2Netty组件.ByteBuf.零拷贝;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import java.nio.charset.Charset;

import static com.changlu.No3Netty入门.No2Netty组件.ByteBuf.ByteBufTest.log;

/**
 * @ClassName Test
 * @Author ChangLu
 * @Date 2022/1/7 22:07
 * @Description Slice：ByteBuf零拷贝体现之一，进行拆分
 */
public class SliceTest {

    public static void main(String[] args) {
        //实际应用测试
//        practicalUse();
//        sliceTest();
        sliceTest2();
    }

    /**
     * 实际应用：零拷贝获取head、body
     */
    public static void practicalUse(){
        final ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(20);
        buffer.writeCharSequence("head,body", Charset.defaultCharset());
        //若是要对某一个ByteBuf进行切割操作，第一部分要的是前5个，第二部分要的是后5个
        //应用场景：对请求body、head进行切割。分割得到的两个部分实际上使用的是原先Buffer的共享内存
        final ByteBuf front = buffer.slice(0, 4);//第一个参数是切割的位置，第二个参数是切割的数量
        log(front);
        final ByteBuf end = buffer.slice(5, 4);
        log(end);
    }


    /**
     * Slice切片得到的ByteBuf进行测试
     */
    public static void sliceTest(){
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(20);
        buffer.writeBytes(new byte[]{1,2,3,4});
        final ByteBuf sliceBuf = buffer.slice(0, 4);
        //1、修改切片得到的ByteBuf也会影响原始的ByteBuf，因为使用的是同一块内存
        sliceBuf.setByte(0,6);
        log(buffer);
        //2、无法对切片进行write操作，会抛出异常IndexOutOfBoundsException
        sliceBuf.writeByte(10);
    }

    /**
     * release()与retain()使用
     */
    public static void sliceTest2(){
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(20);
        buffer.writeBytes(new byte[]{1,2,3,4});
        final ByteBuf sliceBuf = buffer.slice(0, 4);
        //这里引用计数+1，对于原ByteBuf以及切割得到的ByteBuf都有影响，因为是占用的同一块内存
        sliceBuf.retain();//引用计数+1
        buffer.release();
        //若是直接对原ByteBuf进行清理，然后使用切片得到的ByteBuf会抛出异常IllegalReferenceCountException: refCnt: 0
        //若是在release()之后也想正常使用，可以在此之前使用retain()进行引用+1，release()相对于会引用-1，此时就不会真正释放内存，自然也就能欧使用
        log(sliceBuf);
    }

}