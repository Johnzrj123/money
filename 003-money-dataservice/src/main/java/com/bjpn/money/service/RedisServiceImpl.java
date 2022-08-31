package com.bjpn.money.service;

import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Service(interfaceClass = RedisService.class,version="1.0.0",timeout = 20000)
@Component
public class RedisServiceImpl implements RedisService {
    @Autowired(required = false)
    private RedisTemplate redisTemplate;
    @Override
    public void push(String phone, String code) {
        redisTemplate.opsForValue().set(phone,code ,5, TimeUnit.MINUTES);
    }

    @Override
    public String getCode(String phone) {
        return (String)redisTemplate.opsForValue().get(phone);
    }
}
