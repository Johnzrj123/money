package com.bjpn.money.mapper;

import com.bjpn.money.model.RechargeRecord;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
public interface RechargeRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(RechargeRecord record);

    int insertSelective(RechargeRecord record);

    RechargeRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RechargeRecord record);

    int updateByPrimaryKey(RechargeRecord record);

    List<RechargeRecord> selectRechargeRecordByUserId(HashMap<String, Object> parseMap);

    int updateRechargeRecordFail(String out_trade_no);

    RechargeRecord selectRechargeRecordByNo(String out_trade_no);

    int updateRechargeRecordSuccess(@Param("out_trade_no")String out_trade_no,@Param("version") Integer version);

    Long selectRechargeRecordCountByUserId(Integer userId);

    List<RechargeRecord> selectRechargeRecordAnyStatusByUserId(HashMap<String, Object> map);

    List<RechargeRecord> selectRechargeRecordsByZero();
}