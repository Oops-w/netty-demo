package com.wsy.rpc.service.impl;

import com.wsy.rpc.service.HelloService;

/**
 * @author wsy
 * @date 2021/9/20 4:52 下午
 * @Description
 */
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String name) {
        return name + ": say hello";
    }
}
