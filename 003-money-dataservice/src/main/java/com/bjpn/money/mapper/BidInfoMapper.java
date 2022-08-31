package com.bjpn.money.mapper;

import com.bjpn.money.model.BidInfo;
import com.bjpn.money.model.PageModel;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public interface BidInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BidInfo record);

    int insertSelective(BidInfo record);

    BidInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BidInfo record);

    int updateByPrimaryKey(BidInfo record);

    //查询累计成交金额
    Double selectBidMoneySum();

    //根据产品编号，查询产品投资记录
    ArrayList<BidInfo> selectBidInfoByLoanId(HashMap parseMap);

    Long selectBidInfoCount(Integer loanId);

    List<BidInfo> selectBidInfosByLoanId(Integer loanId);

    List<BidInfo> selectBidInfosByUserId(HashMap<String, Object> parseMap);

    Long selectBidInfoCountByUserId(Integer userId);
}