package com.bjpn.money.service;

import com.bjpn.money.model.IncomeRecord;

import java.util.HashMap;
import java.util.List;

/**
 * 收益业务接口
 */
public interface IncomeRecordService {
    /**
     * 生成收益计划
     */
    void generatePlan();

    /**
     * 收益返现
     */
    void incomeBack();

    /**
     * 根据userId分页查询收益记录
     * @param parseMap
     * @return
     */
    List<IncomeRecord> queryMyIncomeRecord(HashMap<String, Object> parseMap);

    /**
     * 根据用户id查询收益记录条数
     * @param userId
     * @return
     */
    Long queryIncomeRecordCountByUserId( Integer userId);

    /**
     * 根据用户id查询收益记录
     * @param map
     * @return
     */
    List<IncomeRecord> queryIncomeRecordByUserId(HashMap<String, Object> map);
}
