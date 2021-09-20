package com.wsy.message;

import lombok.Data;
import lombok.ToString;

/**
 * @author wsy
 */
@Data
@ToString(callSuper = true)
public class LoginRequestMessage extends Message {
    private String username;
    private String password;

    public LoginRequestMessage() {
    }

    public LoginRequestMessage(String username, String password) {
        this.username = username;
        this.password = password;
    }


    @Override
    public void init() {
        this.setMessageType(LoginRequestMessage);
    }
}
