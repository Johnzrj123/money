package com.bjpn.money.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpn.money.mapper.FinanceAccountMapper;
import com.bjpn.money.model.FinanceAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 账户业务实现类
 */
@Service(interfaceClass = FinanceService.class,version="1.0.0",timeout = 20000)
@Component
public class FinanceServiceImpl implements FinanceService {
    @Autowired
    private FinanceAccountMapper financeAccountMapper;
    @Override
    public FinanceAccount queryFinanceByUserId(Integer userId) {
        return financeAccountMapper.selectFinanceByUserId(userId);
    }
}
