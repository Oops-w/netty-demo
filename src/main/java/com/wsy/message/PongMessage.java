package com.wsy.message;

/**
 * @author wsy
 * @date 2021/9/19 10:50 下午
 * @Description
 */
public class PongMessage extends Message {
    @Override
    public void init() {
        this.setMessageType(PongMessage);
    }
}
