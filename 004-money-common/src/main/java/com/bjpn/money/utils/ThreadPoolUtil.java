package com.bjpn.money.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolUtil {
    //获取CPU核数
    static int cpuNums = Runtime.getRuntime().availableProcessors();
    /**
     * 线程池核心池的大小
     */
    private static int corePoolSize = 10;
    /**
     * 线程池的最大线程数
     */
    private static int maximumPoolSize = cpuNums * 5;
    //阻塞队列容量
    private static int queueCapacity = 100;
    //活跃时间，
    private static int keepAliveTimeSecond = 300;

    public static ExecutorService httpApiThreadPool = null;

    static {
        System.out.println("创建线程数:" + corePoolSize + ",最大线程数:" + maximumPoolSize);
        //建立10个核心线程，线程请求个数超过20，则进入队列等待
        httpApiThreadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTimeSecond,
                TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(queueCapacity), new ThreadFactoryBuilder().setNameFormat("PROS-%d").build());
    }
}

