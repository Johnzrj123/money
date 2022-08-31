package com.bjpn.money.service;

import com.bjpn.money.model.LoanInfo;
import com.bjpn.money.model.PageModel;

import java.util.HashMap;
import java.util.List;

public interface LoanInfoService {
    /**
     * 查询产品平均利率
     * @return
     */
    Double queryLoanInfoHistoryRateAvg();

    /**
     * 首页展示：根据产品类型和数量查询产品信息
     * @param parseMap
     * @return
     */
    List<LoanInfo> queryLoanInfoByTypeAndNum(HashMap<String, Object> parseMap);

    /**
     * 查询产品总数
     * @return
     */
    Long queryLoanInfoCount(Integer ptype);

    /**
     * 根据类型和分页模型查询数据
     * @param ptype
     * @param pageModel
     * @return
     */
    List<LoanInfo> queryLoanInfoByTypeAndPageMode(Integer ptype, PageModel pageModel);

    /**
     * 根据id查询产品信息
     * @param loanId
     * @return
     */
    LoanInfo queryLoanInfoById(Integer loanId);

}
