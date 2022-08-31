package com.bjpn.money.service;

import com.bjpn.money.model.BidInfo;
import com.bjpn.money.model.PageModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public interface BidInfoService {
    /**
     * 查询累计成交金额
     * @return
     */
    Double queryBidMoneySum();

    /**
     * 根据产品编号，查询产品投资记录
     * @param loanId
     * @return
     */
    ArrayList<BidInfo> queryBidInfoByLoanId(Integer loanId, PageModel pageModel);

    Long queryBidInfoCount(Integer loanId);

    /**
     * 投资
     * @param parseMap
     * @return
     */
    Integer invest(HashMap<String, Object> parseMap);

    /**
     * 根据用户id,查询用户投资记录（status=）
     * @param parseMap
     * @return
     */
    List<BidInfo> queryMyBidInfo(HashMap<String, Object> parseMap);

    /**
     * 根据用户id,查询用户投资记录总数
     * @param userId
     * @return
     */
    Long queryBidInfoCountByUserId(Integer userId);



    /**
     * 投资排行榜
     * @return
     */
    String bidRanking();
}
