package com.bjpn.money.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.bjpn.money.mapper.BidInfoMapper;
import com.bjpn.money.mapper.FinanceAccountMapper;
import com.bjpn.money.mapper.LoanInfoMapper;
import com.bjpn.money.model.BidInfo;
import com.bjpn.money.model.LoanInfo;
import com.bjpn.money.model.PageModel;
import com.bjpn.money.utils.Constants;
import com.bjpn.money.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.zip.Inflater;

/**
 * 投资业务实现类
 */
@Service(interfaceClass = BidInfoService.class,version="1.0.0",timeout = 20000)
@Component
public class BidInfoServiceImpl implements BidInfoService {
    @Autowired(required = false)
    private RedisTemplate redisTemplate;
    @Autowired
    private BidInfoMapper bidInfoMapper;
    @Autowired
    private LoanInfoMapper loanInfoMapper;
    @Autowired
    private FinanceAccountMapper financeAccountMapper;

    private HashMap<Integer,LoanInfo> map=new HashMap<>();
    //查询累计成交金额
    @Override
    public Double queryBidMoneySum() {
        Double bidMoneySum=(Double) redisTemplate.opsForValue().get(Constants.BID_MONEY_SUM);
        if(bidMoneySum==null){
            synchronized (this){
                bidMoneySum=(Double) redisTemplate.opsForValue().get(Constants.BID_MONEY_SUM);
                if(bidMoneySum==null){
                    bidMoneySum=bidInfoMapper.selectBidMoneySum();
                    redisTemplate.opsForValue().set(Constants.BID_MONEY_SUM, bidMoneySum,20, TimeUnit.SECONDS);
                }
            }
        }
        return bidMoneySum;
    }

    //根据产品编号，查询产品投资记录
    @Override
    public ArrayList<BidInfo> queryBidInfoByLoanId(Integer loanId, PageModel pageModel) {
        HashMap<String,Object> parseMap=new HashMap<>();
        parseMap.put("loanId",loanId);
        parseMap.put("start",(pageModel.getCunPage()-1)*pageModel.getPageSize());
        parseMap.put("content",pageModel.getPageSize());
        return bidInfoMapper.selectBidInfoByLoanId(parseMap);
    }

    @Override
    public Long queryBidInfoCount(Integer loanId) {
        return bidInfoMapper.selectBidInfoCount(loanId);
    }

    //投资
    @Override
    @Transactional
    public Integer invest(HashMap<String, Object> parseMap) {
        /**
         * 1.判断投资金额是否大于剩余可投金额
         * 2.剩余可投金额逐渐减少
         * 3.余额减少
         * 4.判断是否满标
         * 5.添加投资记录
         */
        //判断投资金额是否大于剩余可投金额
        LoanInfo loanInfo = loanInfoMapper.selectByPrimaryKey((Integer) parseMap.get("loanId"));
        if((Double)parseMap.get("bidMoney")>loanInfo.getLeftProductMoney()){
            return Constants.BID_LEFT_MONEY_ERROR;
        }

        //设置版本号
        //parseMap.put("version",loanInfo.getVersion());

        Integer loanId=(Integer)parseMap.get("loanId");
        LoanInfo loanInfo1=map.get(loanId);
        if(loanInfo1==null){
            loanInfo1=new LoanInfo();
            map.put(loanId,loanInfo1);
        }
        int num=0;
        synchronized (map.get(loanId)){
            //可投金额逐渐减少
            num=loanInfoMapper.updateLeftMoneyReduceForInvest(parseMap);
            if(num!=1){
                //手动回滚
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return Constants.BID_LEFT_MONEY_ERROR;
            }
        }

        //余额减少
        num=financeAccountMapper.updateMoneyReduceForInvest(parseMap);
        if(num!=1){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Constants.BID_FINANCE_MONEY_ERROR;
        }

        //判断是否满标
        loanInfo=loanInfoMapper.selectByPrimaryKey((Integer)parseMap.get("loanId"));
        if(loanInfo.getLeftProductMoney()==0 && loanInfo.getProductStatus()==0){
            loanInfo.setProductStatus(1);
            loanInfo.setProductFullTime(new Date());
            num=loanInfoMapper.updateByPrimaryKeySelective(loanInfo);
            if(num!=1){
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return Constants.BID_LOAN_STATUS_ERROR;
            }
        }

        //添加投资记录
        BidInfo bidInfo=new BidInfo();
        bidInfo.setBidMoney((Double)parseMap.get("bidMoney"));
        bidInfo.setBidStatus(1);
        bidInfo.setBidTime(new Date());
        bidInfo.setLoanId((Integer)parseMap.get("loanId"));
        bidInfo.setUid((Integer)parseMap.get("userId"));
        num = bidInfoMapper.insertSelective(bidInfo);
        if(num!=1){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Constants.BID_INFO_ERROR;
        }
        return Constants.BID_INFO_SUCCESS;
    }

    @Override
    public List<BidInfo> queryMyBidInfo(HashMap<String, Object> parseMap) {
        return bidInfoMapper.selectBidInfosByUserId(parseMap);
    }

    @Override
    public Long queryBidInfoCountByUserId(Integer userId) {
        return bidInfoMapper.selectBidInfoCountByUserId(userId);
    }

    @Override
    public String bidRanking() {
        /*
        从redis中获取排行榜
        如果redis中没有，数据库查，查询各用户总投资金额
         */
        Set bidRanking = redisTemplate.opsForZSet().reverseRangeWithScores("bidRanking", 0, 4);
        if(bidRanking==null){
            synchronized (this) {
                bidRanking = redisTemplate.opsForZSet().reverseRangeWithScores("bidRanking", 0, 4);
                if(bidRanking==null) {

                }
            }
        }

        return null;
    }


}
