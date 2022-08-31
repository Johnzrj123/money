package com.bjpn.money.service;


/**
 * redis业务接口
 */
public interface RedisService {
    void push(String phone, String code);

    String getCode(String phone);
}
