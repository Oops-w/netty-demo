package com.wsy.singleton;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.wsy.factory.MyThreadFactory;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author wsy
 * @date 2021/9/18 9:51 上午
 * @Description
 */
public class ThreadPool {
    private static final class Inner {
        private static final ThreadPoolExecutor INSTANCE = new ThreadPoolExecutor(
                5, 10, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<>(5), new MyThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
    }

    public static ThreadPoolExecutor getInstance() {
        return Inner.INSTANCE;
    }
}
