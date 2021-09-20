package com.wsy.rpc.factory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServicesFactory {
    static Map<Class<?>, Object> map = new ConcurrentHashMap<>(16);

    public static Object getInstance(Class<?> interfaceClass) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        // 根据Class创建实例
        try {
            Class<?> clazz = Class.forName("com.wsy.rpc.service.HelloService");
            Object instance = Class.forName("com.wsy.rpc.service.impl.HelloServiceImpl").newInstance();
           
            // 放入 InterfaceClass -> InstanceObject 的映射
            map.put(clazz, instance);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }  
        return map.get(interfaceClass);
    }
}