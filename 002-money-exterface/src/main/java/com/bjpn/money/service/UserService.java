package com.bjpn.money.service;

import com.bjpn.money.model.User;

public interface UserService {
    /**
     * 查询用户人数
     * @return
     */
    Long queryUserCount();

    /**
     * 验证手机号是否已被注册
     * @param phone
     * @return
     */
    int checkPhone(String phone);

    /**
     * 注册
     * @param phone
     * @param loginPassword
     * @return
     */
    User register(String phone, String loginPassword);

    /**
     * 实名认证
     * @param user
     * @return
     */
    int modifyUserByPhoneAndPsd(User user);

    /**
     * 登录
     * @param phone
     * @param loginPassword
     * @return
     */
    User login(String phone, String loginPassword);

    /**
     * 上传头像
     * @param user
     */
    int modifyUserForHeaderImage(User user);
}
