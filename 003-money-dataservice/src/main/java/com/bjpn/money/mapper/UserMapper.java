package com.bjpn.money.mapper;

import com.bjpn.money.model.User;
import org.springframework.stereotype.Repository;

import java.util.HashMap;

@Repository
public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    //查询用户人数
    Long selectUserCount();

    //验证手机号是否已被注册
    int selectCheckPhone(String phone);

    User selectUserByPhoneAndPsd(HashMap<String,String> map);
}