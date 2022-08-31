package com.bjpn.money.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpn.money.mapper.BidInfoMapper;
import com.bjpn.money.mapper.FinanceAccountMapper;
import com.bjpn.money.mapper.IncomeRecordMapper;
import com.bjpn.money.mapper.LoanInfoMapper;
import com.bjpn.money.model.BidInfo;
import com.bjpn.money.model.FinanceAccount;
import com.bjpn.money.model.IncomeRecord;
import com.bjpn.money.model.LoanInfo;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;


import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 收益业务实现类
 */
@Service(interfaceClass = IncomeRecordService.class,version="1.0.0",timeout = 20000)
@Component
public class IncomeRecordServiceImpl implements IncomeRecordService {
    @Autowired
    private LoanInfoMapper loanInfoMapper;
    @Autowired
    private BidInfoMapper bidInfoMapper;
    @Autowired
    private IncomeRecordMapper incomeRecordMapper;
    @Autowired
    private FinanceAccountMapper financeAccountMapper;
    @Override
    @Transactional
    public void generatePlan() {
        /**
         * 1.查询产品列表，获取满标产品
         * 2.遍历集合，查询每个满标产品的投资记录
         * 3.遍历投资记录生成受益计划
         * 4.更新产品状态
         */
        List<LoanInfo> loanInfoList=loanInfoMapper.selectLoanInfoByStatus();

        for(LoanInfo loanInfo:loanInfoList){
            List<BidInfo> bidInfoList = bidInfoMapper.selectBidInfosByLoanId(loanInfo.getId());
            for (BidInfo bidInfo : bidInfoList) {
                IncomeRecord incomeRecord = new IncomeRecord();
                incomeRecord.setBidId(bidInfo.getId());
                incomeRecord.setBidMoney(bidInfo.getBidMoney());

                Date incomeDate = null;
                Double incomeMoney = null;
                if (loanInfo.getProductType() == 0) {
                    incomeDate = DateUtils.addDays(loanInfo.getProductFullTime(), loanInfo.getCycle());
                    incomeMoney = loanInfo.getRate() / 100 / 365 * bidInfo.getBidMoney() * loanInfo.getCycle();
                } else {
                    incomeDate = DateUtils.addMonths(loanInfo.getProductFullTime(), loanInfo.getCycle());
                    incomeMoney = loanInfo.getRate() / 100 / 365 * bidInfo.getBidMoney() * loanInfo.getCycle() * 30;
                }
                /*Date date=loanInfo.getProductFullTime();
                long fullTime=date.getTime();
                long cycleTime=0L;
                if(loanInfo.getProductType()==0) {
                    cycleTime=loanInfo.getCycle().longValue()*24*60*60*1000;
                }else{
                    cycleTime=loanInfo.getCycle().longValue()*30*24*60*60*1000;
                }
                Date incomeDate=new Date(fullTime+cycleTime);*/
                incomeRecord.setIncomeDate(incomeDate);
                incomeRecord.setIncomeMoney(Double.parseDouble(String.format("%.2f", incomeMoney)));

                incomeRecord.setIncomeStatus(0);
                incomeRecord.setLoanId(bidInfo.getLoanId());
                incomeRecord.setUid(bidInfo.getUid());
                int num = incomeRecordMapper.insertSelective(incomeRecord);
                if (num != 1) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return;
                }
            }
            loanInfo.setProductStatus(2);
            int num=loanInfoMapper.updateByPrimaryKeySelective(loanInfo);
            if (num != 1) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return;
            }
        }
    }

    @Override
    @Transactional
    public void incomeBack() {
        /**
         * 查询收益记录，status=0
         * 遍历记录，比较时间
         * 查询时间小于等于当前时间，本金收益返还给用户
         * 修改收益status=1
         */
        List<IncomeRecord> incomeRecordList=incomeRecordMapper.selectIncomeRecordByStatus();
        for(IncomeRecord incomeRecord:incomeRecordList){
            if(new Date().equals(incomeRecord.getIncomeDate()) || new Date().after(incomeRecord.getIncomeDate())){
                Double money=incomeRecord.getBidMoney()+incomeRecord.getIncomeMoney();
                Integer userId=incomeRecord.getUid();
                int num=financeAccountMapper.updateMoneyIncrByUserId(userId,money);
                if(num!=1){
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return;
                }
                incomeRecord.setIncomeStatus(1);
                num=incomeRecordMapper.updateByPrimaryKeySelective(incomeRecord);
                if(num!=1){
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return;
                }
            }
        }
    }

    @Override
    public List<IncomeRecord> queryMyIncomeRecord(HashMap<String, Object> parseMap){
        return incomeRecordMapper.selectIncomeRecordByUserId(parseMap);
    }

    @Override
    public Long queryIncomeRecordCountByUserId(Integer userId) {
        return incomeRecordMapper.selectIncomeRecordCountByUserId(userId);
    }

    @Override
    public List<IncomeRecord> queryIncomeRecordByUserId(HashMap<String, Object> map) {
        return incomeRecordMapper.selectIncomeRecordAnyStatusByUserId(map);
    }
}
