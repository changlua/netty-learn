package com.changlu.message.rpc;

import com.changlu.message.Message;
import lombok.Data;
import lombok.ToString;

/**
 * @ClassName RpcRequestMessage
 * @Author ChangLu
 * @Date 2022/1/16 21:38
 * @Description RPC消息请求对象
 */
@Data
@ToString(callSuper = true)
public class RpcRequestMessage extends Message {

    /**
     * 接口全限定名
     */
    private String interfaceName;

    /**
     * 调用接口的方法名
     */
    private String methodName;

    /**
     * 方法返回类型
     */
    private Class<?> returnType;

    /**
     * 方法参数类型数组
     */
    private Class[] parameterTypes;

    /**
     * 方法调用参数值
     */
    private Object[] parameterValue;

    public RpcRequestMessage(int sequenceId,String interfaceName, String methodName, Class<?> returnType, Class[] parameterTypes, Object[] parameterValue) {
        super.setSequenceId(sequenceId);
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.returnType = returnType;
        this.parameterTypes = parameterTypes;
        this.parameterValue = parameterValue;
    }

    @Override
    public int getMessageType() {
        return RPC_MESSAGE_TYPE_REQUEST;
    }
}