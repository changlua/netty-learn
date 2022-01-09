package com.changlu.No4Netty进阶.No2协议设计与解析.No1常用协议示例.No3自定义协议示例.Test;

import lombok.Data;
import lombok.ToString;

/**
 * @ClassName OwnMessage
 * @Author ChangLu
 * @Date 2022/1/9 18:07
 * @Description 登陆请求消息类
 */
@Data
@ToString(callSuper = true)
public class LoginRequestMessage extends Message{
    private String username;
    private String password;

    public LoginRequestMessage() {
    }

    public LoginRequestMessage(String username, String password) {
        this.username = username;
        this.password = password;
    }


    @Override
    public int getMessageType() {
        return LoginRequestMessage;
    }
}