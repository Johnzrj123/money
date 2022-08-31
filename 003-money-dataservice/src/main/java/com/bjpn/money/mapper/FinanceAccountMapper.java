package com.bjpn.money.mapper;

import com.bjpn.money.model.FinanceAccount;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashMap;

@Repository
public interface FinanceAccountMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(FinanceAccount record);

    int insertSelective(FinanceAccount record);

    FinanceAccount selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(FinanceAccount record);

    int updateByPrimaryKey(FinanceAccount record);

    FinanceAccount selectFinanceByUserId(Integer userId);

    //余额减少
    int updateMoneyReduceForInvest(HashMap<String, Object> parseMap);

    int updateMoneyIncrByUserId(@Param("userId") Integer userId,@Param("money") Double money);

    int updateMoneyRechargeSuccess(@Param("userId") Integer userId,@Param("money") Double money);
}