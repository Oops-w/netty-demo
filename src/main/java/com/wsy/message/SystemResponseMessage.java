package com.wsy.message;

/**
 * @author wsy
 * @date 2021/9/19 8:15 下午
 * @Description system notify
 */
public class SystemResponseMessage extends AbstractResponseMessage {
    public SystemResponseMessage() {

    }

    public SystemResponseMessage(boolean success, String reason) {
        super(success, reason);
    }

    @Override
    public void init() {
        this.setMessageType(SystemResponseMessage);
    }
}
