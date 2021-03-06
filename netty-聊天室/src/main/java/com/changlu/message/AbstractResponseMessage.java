package com.changlu.message;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public abstract class AbstractResponseMessage extends Message {
    private boolean success = true;
    private String reason = "成功！";

    public AbstractResponseMessage() {
    }

    public AbstractResponseMessage(boolean success, String reason) {
        this.success = success;
        this.reason = reason;
    }
}
