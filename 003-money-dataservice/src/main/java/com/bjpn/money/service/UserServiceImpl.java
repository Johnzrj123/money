package com.bjpn.money.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpn.money.mapper.FinanceAccountMapper;
import com.bjpn.money.mapper.UserMapper;
import com.bjpn.money.model.FinanceAccount;
import com.bjpn.money.model.User;
import com.bjpn.money.utils.Constants;
import com.bjpn.money.utils.ThreadPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * 用户业务实现类
 */
@Service(interfaceClass = UserService.class,version="1.0.0",timeout = 20000)
@Component
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FinanceAccountMapper financeAccountMapper;

    @Autowired(required = false)
    private RedisTemplate redisTemplate;
    //查询用户人数
    @Override
    public Long queryUserCount() {
        Long userCount=(Long)redisTemplate.opsForValue().get(Constants.USER_COUNT);
        if(userCount==null){
            synchronized (this) {
                userCount=(Long)redisTemplate.opsForValue().get(Constants.USER_COUNT);
                if(userCount==null) {
                    userCount = userMapper.selectUserCount();
                    redisTemplate.opsForValue().set(Constants.USER_COUNT, userCount, 20, TimeUnit.SECONDS);
                }
            }
        }
        return userCount;
    }

    //验证手机号是否已被注册
    @Override
    public int checkPhone(String phone) {
        return userMapper.selectCheckPhone(phone);
    }

    @Override
    public User register(String phone, String loginPassword) {
        User user=new User();
        user.setAddTime(new Date());
        user.setPhone(phone);
        user.setLoginPassword(loginPassword);
        int num=userMapper.insertSelective(user);

        /*if(num==1){
            //在项目中加入线程池
            new Thread(new Runnable() {
                @Override
                public void run() {
                    FinanceAccount financeAccount=new FinanceAccount();
                    financeAccount.setUid(user.getId());
                    financeAccount.setAvailableMoney(888d);
                    financeAccountMapper.insertSelective(financeAccount);
                }
            }).start();
        }*/
        if(num==1){
            ThreadPoolUtil.httpApiThreadPool.submit(new Thread(new Runnable() {
                @Override
                public void run() {
                    FinanceAccount financeAccount=new FinanceAccount();
                    financeAccount.setUid(user.getId());
                    financeAccount.setAvailableMoney(888d);
                    financeAccountMapper.insertSelective(financeAccount);
                }
            }));
        }
        return user;
    }

    @Override
    public int modifyUserByPhoneAndPsd(User user) {
        return userMapper.updateByPrimaryKeySelective(user);
    }

    @Override
    public User login(String phone, String loginPassword) {
        HashMap<String,String> map=new HashMap<>();
        map.put("phone",phone);
        map.put("loginPassword", loginPassword);
        User user=userMapper.selectUserByPhoneAndPsd(map);
        System.out.println(user);
        if(user!=null){
            ThreadPoolUtil.httpApiThreadPool.submit(new Thread(new Runnable() {
                @Override
                public void run() {
                    user.setLastLoginTime(new Date());
                    userMapper.updateByPrimaryKeySelective(user);
                }
            }));
        }
        return user;
    }

    @Override
    public int modifyUserForHeaderImage(User user) {
        return userMapper.updateByPrimaryKeySelective(user);
    }
}
