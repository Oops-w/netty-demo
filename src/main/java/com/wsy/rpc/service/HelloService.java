package com.wsy.rpc.service;

/**
 * @author wsy
 * @date 2021/9/20 4:52 下午
 * @Description
 */
public interface HelloService {
    /**
     * say hello
     * @param name sender name
     * @return said
     */
    String sayHello(String name);
}
