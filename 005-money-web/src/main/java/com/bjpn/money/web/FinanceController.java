package com.bjpn.money.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.bjpn.money.model.FinanceAccount;
import com.bjpn.money.model.User;
import com.bjpn.money.service.FinanceService;
import com.bjpn.money.utils.Result;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class FinanceController {
    @Reference(interfaceClass = FinanceService.class,version="1.0.0",timeout = 20000)
    private FinanceService financeService;

    @GetMapping("/loan/page/queryFinance")
    @ResponseBody
    public Object queryFinance(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("loginUser");
        if (!ObjectUtils.allNotNull(user)) {
            return Result.error("请先登录再查询账户信息");
        }
        FinanceAccount financeAccount = financeService.queryFinanceByUserId(user.getId());
        if (!ObjectUtils.allNotNull(financeAccount)) {
            return Result.error("系统繁忙");
        }
        return Result.success(financeAccount.getAvailableMoney() + "");
    }
}
