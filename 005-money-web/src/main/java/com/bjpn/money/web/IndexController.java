package com.bjpn.money.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.bjpn.money.model.LoanInfo;
import com.bjpn.money.service.BidInfoService;
import com.bjpn.money.service.LoanInfoService;
import com.bjpn.money.service.UserService;
import com.bjpn.money.utils.Constants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

@Controller
public class IndexController {
    @Reference(interfaceClass = LoanInfoService.class,version="1.0.0",timeout = 20000)
    private LoanInfoService loanInfoService;
    @Reference(interfaceClass = UserService.class,version="1.0.0",timeout = 20000)
    private UserService userService;
    @Reference(interfaceClass = BidInfoService.class,version="1.0.0",timeout = 20000)
    private BidInfoService bidInfoService;

    @GetMapping("/index")
    public String index(Model model, HttpServletRequest request){
        request.getSession();
        //年华收益率
        Double loanInfoHistoryRateAvg=loanInfoService.queryLoanInfoHistoryRateAvg();
        model.addAttribute(Constants.LOAN_INFO_HISTORY_RATE_AVG,loanInfoHistoryRateAvg);
        //用户量
        Long userCount=userService.queryUserCount();
        model.addAttribute(Constants.USER_COUNT,userCount);
        //累计成交额
        Double bidMoneySum=bidInfoService.queryBidMoneySum();
        model.addAttribute(Constants.BID_MONEY_SUM,bidMoneySum );

        HashMap<String,Object> parseMap=new HashMap<>();
        //新手宝
        parseMap.put("ptype", 0);
        parseMap.put("start",0);
        parseMap.put("content",1);
        List<LoanInfo> loanInfoList_X=loanInfoService.queryLoanInfoByTypeAndNum(parseMap);
        model.addAttribute("loanInfoList_X", loanInfoList_X);
        //优选标
        parseMap.put("ptype",1);
        parseMap.put("start",0);
        parseMap.put("content",4);
        List<LoanInfo> loanInfoList_Y=loanInfoService.queryLoanInfoByTypeAndNum(parseMap);
        model.addAttribute("loanInfoList_Y",loanInfoList_Y );
        //散户
        parseMap.put("ptype", 2);
        parseMap.put("start",0);
        parseMap.put("content",8);
        List<LoanInfo> loanInfoList_S=loanInfoService.queryLoanInfoByTypeAndNum(parseMap);
        model.addAttribute("loanInfoList_S",loanInfoList_S);
        return "index";
    }
}
