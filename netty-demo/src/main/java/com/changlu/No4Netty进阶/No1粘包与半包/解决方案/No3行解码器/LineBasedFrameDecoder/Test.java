package com.changlu.No4Netty进阶.No1粘包与半包.解决方案.No3行解码器.LineBasedFrameDecoder;

import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;

/**
 * @ClassName Test
 * @Author ChangLu
 * @Date 2022/1/9 0:14
 * @Description TODO
 */
public class Test {

    public static void main(String[] args) {
        final EmbeddedChannel channel = new EmbeddedChannel(new LineBasedFrameDecoder(10));


    }

}