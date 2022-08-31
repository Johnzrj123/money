package com.bjpn.money;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.bjpn.money.model.RechargeRecord;
import com.bjpn.money.model.User;
import com.bjpn.money.service.IncomeRecordService;
import com.bjpn.money.service.RechargeRecordService;
import com.bjpn.money.utils.Constants;
import com.bjpn.money.utils.HttpClientUtils;
import org.apache.commons.codec.binary.StringUtils;
import org.jboss.netty.util.internal.StringUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.List;

@Component
public class SpringTask {
    @Reference(interfaceClass = IncomeRecordService.class,version="1.0.0",timeout = 20000)
    private IncomeRecordService incomeRecordService;
    @Reference(interfaceClass = RechargeRecordService.class,version="1.0.0",timeout = 20000)
    private RechargeRecordService rechargeRecordService;

    //生成收益计划
    @Scheduled(cron="0/5 * * * * ?")
    public void generatePlan(){
        /**
         * 1.查询产品列表，获取满标产品
         * 2.遍历集合，查询每个满标产品的投资记录
         * 3.遍历投资记录生成受益计划
         * 4.更新产品状态
         */
        System.out.println("---generate start---");
        incomeRecordService.generatePlan();
        System.out.println("---generate end---");
    }

    //收益返现
    @Scheduled(cron="0/5 * * * * ?")
    public void incomeBack(){
        System.out.println("---income start---");
        incomeRecordService.incomeBack();
        System.out.println("---income end---");
    }

    //处理订单
    @Scheduled(cron="0/5 * * * * ?")
    public void doRechargeRecord(){
        /**
         * 1.查询状态为0的订单
         * 2.遍历订单，向支付宝获取交易状态
         * 3. 根据交易处理订单：修改状态，更新余额
         * 4.响应用户信息
         */
        System.out.println("---start recharge---");

        //查询状态为0的订单
        List<RechargeRecord> rechargeRecordList=rechargeRecordService.queryRechargeRecordsByZero();
        //遍历订单，向支付宝获取交易状态
        for (RechargeRecord rechargeRecord : rechargeRecordList) {
            String result=null;
            try {
                result= HttpClientUtils.doGet("http://localhost:9007/007-money-pay//loan/pay/queryOrder?out_trade_no="+rechargeRecord.getRechargeNo());
                JSONObject jsonObject=JSONObject.parseObject(result).getJSONObject("alipay_trade_query_response");
                String code=jsonObject.getString("code");
                if(StringUtils.equals("10000",code)){
                    String trade_status=jsonObject.getString("trade_status");
                    if(StringUtils.equals("WAIT_BUYER_PAY",trade_status)){
                        //发短信：请支付订单
                    }
                    if(StringUtils.equals("TRADE_CLOSED", trade_status)){
                        //修改订单状态为2，响应失败信息
                        rechargeRecordService.rechargeFail(rechargeRecord.getRechargeNo());
                    }
                    if(StringUtils.equals("TRADE_SUCCESS",jsonObject.getString("trade_status"))){
                        HashMap<String,Object> parseMap=new HashMap<>();
                        parseMap.put("out_trade_no",rechargeRecord.getRechargeNo());
                        parseMap.put("total_amount",rechargeRecord.getRechargeMoney());
                        parseMap.put("userId",rechargeRecord.getUid());
                        parseMap.put("version",rechargeRecord.getVersion());
                        //修改订单状态为1，余额增加，发短信
                        rechargeRecordService.rechargeSuccess(parseMap);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("---end recharge---");
    }
}
