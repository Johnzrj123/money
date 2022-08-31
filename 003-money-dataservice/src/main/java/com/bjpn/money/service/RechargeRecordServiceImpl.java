package com.bjpn.money.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpn.money.mapper.FinanceAccountMapper;
import com.bjpn.money.mapper.RechargeRecordMapper;
import com.bjpn.money.model.FinanceAccount;
import com.bjpn.money.model.RechargeRecord;
import com.bjpn.money.utils.Result;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 充值业务实现类
 */
@Service(interfaceClass = RechargeRecordService.class,version="1.0.0",timeout = 20000)
@Component
public class RechargeRecordServiceImpl implements RechargeRecordService {
    @Autowired
    private RechargeRecordMapper rechargeRecordMapper;
    @Autowired
    private FinanceAccountMapper financeAccountMapper;

    @Override
    public RechargeRecord recharge(Integer userId, Double rechargeMoney) {
        RechargeRecord rechargeRecord=new RechargeRecord();
        rechargeRecord.setUid(userId);
        rechargeRecord.setRechargeMoney(rechargeMoney);
        rechargeRecord.setRechargeDesc("支付宝支付");
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
        String format = sdf.format(new Date());
        //todo:多终端同时下单，订单号唯一
        rechargeRecord.setRechargeNo(format+ Result.generateCode(3) +userId+Result.generateCode(3));
        rechargeRecord.setRechargeStatus(0+"");
        rechargeRecord.setRechargeTime(new Date());
        int num=rechargeRecordMapper.insertSelective(rechargeRecord);
        if(num!=1){
            return null;
        }
        return rechargeRecord;
    }

    @Override
    public List<RechargeRecord> queryMyRechargeRecord(HashMap<String, Object> parseMap) {
        return rechargeRecordMapper.selectRechargeRecordByUserId(parseMap);
    }

    @Override
    public int rechargeFail(String out_trade_no) {
        return rechargeRecordMapper.updateRechargeRecordFail(out_trade_no);
    }

    @Override
    @Transactional
    public int rechargeSuccess(HashMap<String, Object> parseMap) {
        Integer userId=(Integer)parseMap.get("userId");
        RechargeRecord rechargeRecord=null;
        if(!ObjectUtils.allNotNull(userId)){
            rechargeRecord=rechargeRecordMapper.selectRechargeRecordByNo((String)parseMap.get("out_trade_no"));
            parseMap.put("userId",rechargeRecord.getUid());
        }
        //修改状态
        /*if(rechargeRecord!=null){
            rechargeRecord.setRechargeStatus("1");
            rechargeRecordMapper.updateByPrimaryKeySelective(rechargeRecord);
        }else{
            rechargeRecordMapper.updateRechargeRecordSuccess((String)parseMap.get("out_trade_no"));
        }*/
        int num=rechargeRecordMapper.updateRechargeRecordSuccess((String)parseMap.get("out_trade_no"),(Integer)parseMap.get("version"));
        if(num!=1){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return num;
        }
        num = financeAccountMapper.updateMoneyRechargeSuccess((Integer) parseMap.get("userId"), (Double) parseMap.get("total_amount"));
        if (num != 1) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return num;
        }


        //更新余额

        return num;
    }

    @Override
    public Long queryRechargeRecordCountByUserId(Integer userId) {
        return rechargeRecordMapper.selectRechargeRecordCountByUserId(userId);
    }

    @Override
    public List<RechargeRecord> queryRechargeRecordByUserId(HashMap<String, Object> map) {

        return rechargeRecordMapper.selectRechargeRecordAnyStatusByUserId(map);
    }

    @Override
    public List<RechargeRecord> queryRechargeRecordsByZero() {
        return rechargeRecordMapper.selectRechargeRecordsByZero();
    }
}
