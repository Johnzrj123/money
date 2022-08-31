package com.bjpn.money.mapper;

import com.bjpn.money.model.LoanInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.HashMap;
import java.util.List;

@Repository
public interface LoanInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(LoanInfo record);

    int insertSelective(LoanInfo record);

    LoanInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(LoanInfo record);

    int updateByPrimaryKey(LoanInfo record);

    //查询产品利率
    Double selectLoanInfoHistoryRateAvg();

    //首页展示：根据产品类型和数量查询产品信息
    List<LoanInfo> selectLoanInfoByTypeAndNum(HashMap<String, Object> parseMap);

    //查询产品数量
    Long selectLoanInfoCount(Integer ptype);

    //剩余可投金额减少
    int updateLeftMoneyReduceForInvest(HashMap<String, Object> parseMap);

    //查询满标产品
    List<LoanInfo> selectLoanInfoByStatus();
}