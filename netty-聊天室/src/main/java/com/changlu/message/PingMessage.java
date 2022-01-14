package com.changlu.message;

/**
 * @ClassName PingMessage
 * @Author ChangLu
 * @Date 2022/1/14 16:54
 * @Description 用于发送心跳
 */
public class PingMessage extends Message {
    @Override
    public int getMessageType() {
        return PingMessage;
    }
}
