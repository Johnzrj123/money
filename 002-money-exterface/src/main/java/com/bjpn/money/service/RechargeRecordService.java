package com.bjpn.money.service;


import com.bjpn.money.model.RechargeRecord;

import java.util.HashMap;
import java.util.List;

/**
 * 充值业务接口
 */
public interface RechargeRecordService {
    /**
     * 充值
     * @param userId
     * @param rechargeMoney
     * @return
     */
    RechargeRecord recharge(Integer userId, Double rechargeMoney);

    /**
     * 根据用户id查询充值记录(status=1)
     * @param parseMap
     * @return
     */
    List<RechargeRecord> queryMyRechargeRecord(HashMap<String, Object> parseMap);

    /**
     * 充值失败
     * @param out_trade_no
     * @return
     */
    int rechargeFail(String out_trade_no);

    /**
     * 充值成功
     * @param parseMap
     * @return
     */
    int rechargeSuccess(HashMap<String, Object> parseMap);

    /**
     * 根据用户id,查找充值记录条数
     * @param id
     * @return
     */
    Long queryRechargeRecordCountByUserId(Integer id);

    /**
     * 根据用户id查询充值记录
     * @param map
     * @return
     */
    List<RechargeRecord> queryRechargeRecordByUserId(HashMap<String, Object> map);

    /**
     * 定时器：查询状态为0的订单
     * @return
     */
    List<RechargeRecord> queryRechargeRecordsByZero();

}
