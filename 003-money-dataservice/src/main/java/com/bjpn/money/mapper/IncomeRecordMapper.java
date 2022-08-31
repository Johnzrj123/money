package com.bjpn.money.mapper;

import com.bjpn.money.model.IncomeRecord;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
public interface IncomeRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(IncomeRecord record);

    int insertSelective(IncomeRecord record);

    IncomeRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(IncomeRecord record);

    int updateByPrimaryKey(IncomeRecord record);

    List<IncomeRecord> selectIncomeRecordByStatus();

    List<IncomeRecord> selectIncomeRecordByUserId(HashMap<String, Object> parseMap);

    Long selectIncomeRecordCountByUserId(Integer userId);

    List<IncomeRecord> selectIncomeRecordAnyStatusByUserId(HashMap<String, Object> map);
}