package com.bjpn.money.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommonPools {
    private static ExecutorService instance= Executors.newFixedThreadPool(16);
    public static ExecutorService getInstance(){
        return instance;
    }
}
