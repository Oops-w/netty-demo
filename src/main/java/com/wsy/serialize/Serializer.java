package com.wsy.serialize;

/**
 * @author wsy
 * @date 2021/9/20 9:06 上午
 * @Description
 */
public interface Serializer {
    /**
     * serialize
     * @param object serialize object
     * @param <T>
     * @return serialize then byte array
     */
    <T> byte[] serialize(T object);

    /**
     * deserialize
     * @param clazz transfer to clazz object
     * @param bytes byte source array
     * @param <T>
     * @return clazz object
     */
    <T> T deserialize(Class<T> clazz, byte[] bytes);
}
