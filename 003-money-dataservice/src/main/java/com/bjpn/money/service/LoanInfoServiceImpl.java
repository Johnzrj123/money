package com.bjpn.money.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpn.money.mapper.LoanInfoMapper;
import com.bjpn.money.model.LoanInfo;
import com.bjpn.money.model.PageModel;
import com.bjpn.money.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 产品业务实现类
 */
@Service(interfaceClass = LoanInfoService.class,version="1.0.0",timeout = 20000)
@Component
public class LoanInfoServiceImpl implements LoanInfoService {
    @Autowired
    private LoanInfoMapper loanInfoMapper;
    @Autowired(required = false)
    private RedisTemplate redisTemplate;
    //查询产品利率
    @Override
    public Double queryLoanInfoHistoryRateAvg() {
        Double loanInfoHistoryRateAvg=(Double) redisTemplate.opsForValue().get(Constants.LOAN_INFO_HISTORY_RATE_AVG);
        if(loanInfoHistoryRateAvg==null){
            synchronized (this){
                loanInfoHistoryRateAvg=(Double) redisTemplate.opsForValue().get(Constants.LOAN_INFO_HISTORY_RATE_AVG);
                if(loanInfoHistoryRateAvg==null){
                    System.out.println("---数据库查询---");
                    loanInfoHistoryRateAvg=loanInfoMapper.selectLoanInfoHistoryRateAvg();
                    redisTemplate.opsForValue().set(Constants.LOAN_INFO_HISTORY_RATE_AVG, loanInfoHistoryRateAvg,20, TimeUnit.SECONDS);
                }else{
                    System.out.println("---缓存命中---");
                }
            }
        }else{
            System.out.println("---缓存命中---");
        }
        return loanInfoHistoryRateAvg;
    }

    //首页展示：根据产品类型和数量查询产品信息
    @Override
    public List<LoanInfo> queryLoanInfoByTypeAndNum(HashMap<String, Object> parseMap) {
        return loanInfoMapper.selectLoanInfoByTypeAndNum(parseMap);
    }

    //查询产品总数
    @Override
    public Long queryLoanInfoCount(Integer ptype) {
        Long loanInfoCount=(Long)redisTemplate.opsForValue().get("loanInfoCount");
        if(loanInfoCount==null){
            synchronized (this) {
                loanInfoCount=(Long)redisTemplate.opsForValue().get("loanInfoCount");
                if(loanInfoCount==null) {
                    loanInfoCount = loanInfoMapper.selectLoanInfoCount(ptype);
                    redisTemplate.opsForValue().set("loanInfoCount", loanInfoCount, 30,TimeUnit.SECONDS);
                }
            }
        }
        return loanInfoCount;
    }

    //根据类型和分页模型查询数据
    @Override
    public List<LoanInfo> queryLoanInfoByTypeAndPageMode(Integer ptype, PageModel pageModel) {
        HashMap<String,Object> parseMap=new HashMap<>();
        parseMap.put("ptype", ptype);
        parseMap.put("start",(pageModel.getCunPage()-1)*pageModel.getPageSize());
        parseMap.put("content",pageModel.getPageSize());
        return loanInfoMapper.selectLoanInfoByTypeAndNum(parseMap);
    }

    //根据id查询产品信息
    @Override
    public LoanInfo queryLoanInfoById(Integer loanId) {
        return loanInfoMapper.selectByPrimaryKey(loanId);
    }
}
