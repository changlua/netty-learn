package com.changlu.message;

/**
 * @ClassName QuitMemberResponseMessage
 * @Author ChangLu
 * @Date 2022/1/14 21:43
 * @Description 指定用户下线
 */
public class QuitMemberResponseMessage extends AbstractResponseMessage{

    public QuitMemberResponseMessage(boolean success, String reason) {
        super(success, reason);
    }

    @Override
    public int getMessageType() {
        return QuitMemberResponseMessage;
    }
}