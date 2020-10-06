package com.lightingsui.linuxwatcher.config;

import cn.hutool.core.thread.NamedThreadFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池配置
 *
 * @author ：隋亮亮
 * @since ：2020/10/6 8:31
 */
public class ThreadPoolConfig {
    public static ThreadPoolExecutor executor;

    static {
        executor = new ThreadPoolExecutor(20, 30, 1000, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(100), new NamedThreadFactory("time", false));
    }
}
