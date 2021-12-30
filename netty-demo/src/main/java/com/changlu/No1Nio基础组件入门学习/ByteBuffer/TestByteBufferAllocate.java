package com.changlu.No1Nio基础组件入门学习.ByteBuffer;

import java.nio.ByteBuffer;

/**
 * @ClassName TestByteBufferAllocate
 * @Author ChangLu
 * @Date 2021/12/16 20:41
 * @Description ByteBufferAllocate：开辟空间
 */
public class TestByteBufferAllocate {
    public static void main(String[] args) {
        //开启堆内存
        System.out.println(ByteBuffer.allocate(10).getClass());
        //开辟直接内存
        System.out.println(ByteBuffer.allocateDirect(10).getClass());
        /*
        class java.nio.HeapByteBuffer    - java 堆内存，读写效率较低，受到 GC 的影响
        class java.nio.DirectByteBuffer  - 直接内存，读写效率高（少一次拷贝），不会受 GC 影响，分配的效率低
         */
    }
}