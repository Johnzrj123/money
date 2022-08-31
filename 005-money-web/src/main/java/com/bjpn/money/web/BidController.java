package com.bjpn.money.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.bjpn.money.model.BidInfo;
import com.bjpn.money.model.LoanInfo;
import com.bjpn.money.model.PageModel;
import com.bjpn.money.model.User;
import com.bjpn.money.service.BidInfoService;
import com.bjpn.money.service.LoanInfoService;
import com.bjpn.money.utils.CommonPools;
import com.bjpn.money.utils.Constants;
import com.bjpn.money.utils.Result;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Controller
public class BidController {
    @Reference(interfaceClass = LoanInfoService.class,version="1.0.0",timeout = 20000)
    private LoanInfoService loanInfoService;
    @Reference(interfaceClass = BidInfoService.class,version="1.0.0",timeout = 20000)
    private BidInfoService bidInfoService;

    @PostMapping("/loan/page/invest")
    @ResponseBody
    public Object invest(@RequestParam(name="bidMoney") Double bidMoney,
                         @RequestParam(name="loanId") Integer loanId,
                         HttpServletRequest request){
        User user=(User)request.getSession().getAttribute(Constants.LOGIN_USER);
        if(!ObjectUtils.allNotNull(user)){
            return Result.error("请先登录后再投资");
        }
        if(!ObjectUtils.allNotNull(user.getName(),user.getIdCard())){
            return Result.error("请先实名认证后再投资");
        }
        //todo:基础验证、业务验证
        LoanInfo loanInfo = loanInfoService.queryLoanInfoById(loanId);
        if(bidMoney>loanInfo.getBidMaxLimit() || bidMoney<loanInfo.getBidMinLimit()){
            return Result.error("可投金额在"+loanInfo.getBidMinLimit()+"--"+loanInfo.getBidMaxLimit()+"之间");
        }
        if(bidMoney>loanInfo.getLeftProductMoney()){
            return Result.error("可投金额在"+loanInfo.getBidMinLimit()+"--"+loanInfo.getLeftProductMoney()+"之间");
        }

        //投资
        HashMap<String,Object> parseMap=new HashMap<>();
        parseMap.put("bidMoney",bidMoney);
        parseMap.put("loanId",loanId);
        parseMap.put("userId",user.getId());
        Integer result=0;
        //模拟千人投资
        /*for (int i = 0; i < 100; i++) {
            CommonPools.getInstance().submit(new Runnable() {
                @Override
                public void run() {
                    bidInfoService.invest(parseMap);
                }
            });
        }*/
        try {
            result = bidInfoService.invest(parseMap);
        }catch(Exception e){
            e.printStackTrace();
            return Result.error("系统异常");
        }
        if(Objects.equals(result, Constants.BID_LEFT_MONEY_ERROR)){
            return Result.error("剩余可投资金额减少失败");
        }
        if(Objects.equals(result, Constants.BID_FINANCE_MONEY_ERROR)){
            return Result.error("账户金额减少失败");
        }
        if(Objects.equals(result, Constants.BID_LOAN_STATUS_ERROR)){
            return Result.error("产品状态更新失败");
        }
        if(Objects.equals(result, Constants.BID_INFO_ERROR)){
            return Result.error("添加投资记录失败");
        }
        return Result.success();
    }

    @GetMapping("/loan/myInvest")
    public String myInvest(Long cunPage,Model model,HttpServletRequest request){
        PageModel investPage=(PageModel)request.getSession().getAttribute("investPage");
        if(investPage==null){
            investPage=new PageModel(6);
            request.getSession().setAttribute("investPage", investPage);
        }
        if(cunPage==null || cunPage<1){
            cunPage=investPage.getFirstPage().longValue();
        }
        User user=(User)request.getSession().getAttribute(Constants.LOGIN_USER);
        Long totalCount=bidInfoService.queryBidInfoCountByUserId(user.getId());
        investPage.setTotalCount(totalCount);
        if(totalCount!=0 && investPage.getTotalPage()<cunPage){
            cunPage=investPage.getTotalPage();
        }
        investPage.setCunPage(cunPage);
        HashMap<String,Object> map=new HashMap<>();
        map.put("userId",user.getId());
        map.put("start",(cunPage-1)*investPage.getPageSize());
        map.put("content",investPage.getPageSize());
        List<BidInfo> bidInfoList= bidInfoService.queryMyBidInfo(map);
        model.addAttribute("bidInfoList",bidInfoList );
        return "myInvest";
    }

}
