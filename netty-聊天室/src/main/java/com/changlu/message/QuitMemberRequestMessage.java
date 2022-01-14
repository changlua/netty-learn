package com.changlu.message;

import lombok.Data;
import lombok.ToString;

/**
 * @ClassName QuitMemberRequestMessage
 * @Author ChangLu
 * @Date 2022/1/14 21:42
 * @Description 指定用户下线
 */
@Data
@ToString(callSuper=true)
public class QuitMemberRequestMessage extends Message{

    private String username;

    public QuitMemberRequestMessage(String username) {
        this.username = username;
    }

    @Override
    public int getMessageType() {
        return QuitMemberRequestMessage;
    }
}