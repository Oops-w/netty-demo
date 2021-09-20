package com.wsy.message;

import lombok.Data;
import lombok.ToString;

/**
 * @author wsy
 */
@Data
@ToString(callSuper = true)
public class LoginResponseMessage extends AbstractResponseMessage {
    @Override
    public void init() {
        this.setMessageType(LoginResponseMessage);
    }
}
