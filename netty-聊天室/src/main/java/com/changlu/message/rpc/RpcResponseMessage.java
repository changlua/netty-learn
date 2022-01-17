package com.changlu.message.rpc;

import com.changlu.message.Message;
import lombok.Data;
import lombok.ToString;

/**
 * @ClassName RpcResponseMessage
 * @Author ChangLu
 * @Date 2022/1/16 21:38
 * @Description RPC消息请求对象
 */
@Data
@ToString(callSuper = true)
public class RpcResponseMessage extends Message {

    /**
     * 返回值
     */
    private Object returnValue;

    /**
     * 异常值
     */
    private Exception exceptionValue;

    public RpcResponseMessage() {
    }

    public RpcResponseMessage(Object returnValue, Exception exceptionValue) {
        this.returnValue = returnValue;
        this.exceptionValue = exceptionValue;
    }

    @Override
    public int getMessageType() {
        return RPC_MESSAGE_TYPE_RESPONSE;
    }
}