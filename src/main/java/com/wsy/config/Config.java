package com.wsy.config;

import com.wsy.serialize.SerializerAlgorithm;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 使用配置文件 获取 编解码方法
 *
 * @author wsy
 */
public class Config {

    static Properties properties;

    static {
        try (InputStream in = Config.class.getResourceAsStream("/application.properties")) {

            properties = new Properties();
            properties.load(in);

        } catch (IOException e) {

            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     *
     * @return return server port
     */
    public static int getServerPort() {
        final String value = properties.getProperty("server.port");
        // default 8080 port
        if (value == null) {
            return 8080;
        } else {
            return Integer.parseInt(value);
        }
    }

    /**
     * @return by profile load serializer algorithm
     */
    public static SerializerAlgorithm getSerializerAlgorithm() {

        final String value = properties.getProperty("serializer.algorithm");
        // default jdk serialize
        if (value == null) {
            return SerializerAlgorithm.JDK;
        } else {
            // by profile get serializer algorithm
            return SerializerAlgorithm.valueOf(value);
        }
    }
}
