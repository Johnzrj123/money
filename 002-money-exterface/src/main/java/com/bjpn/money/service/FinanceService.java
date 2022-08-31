package com.bjpn.money.service;

import com.bjpn.money.model.FinanceAccount;

/**
 * 账户业务接口
 */
public interface FinanceService {
    /**
     * 登录后根据用户编号查询账户余额
     * @param id
     * @return
     */
    FinanceAccount queryFinanceByUserId(Integer id);
}
