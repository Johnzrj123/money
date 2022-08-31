package com.bjpn.money.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.bjpn.money.model.IncomeRecord;
import com.bjpn.money.model.PageModel;
import com.bjpn.money.model.User;
import com.bjpn.money.service.IncomeRecordService;
import com.bjpn.money.utils.Constants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

@Controller
public class IncomeController {
    @Reference(interfaceClass = IncomeRecordService.class,version="1.0.0",timeout = 20000)
    private IncomeRecordService incomeRecordService;

    @GetMapping("/loan/myIncome")
    public String myIncome(Long cunPage, Model model, HttpServletRequest request){
        PageModel incomePage=(PageModel)request.getSession().getAttribute("incomePage");
        if(incomePage==null){
            incomePage=new PageModel(6);
            request.getSession().setAttribute("incomePage",incomePage );
        }
        if(cunPage==null || cunPage<1){
            cunPage=incomePage.getFirstPage().longValue();
        }
        User user=(User)request.getSession().getAttribute(Constants.LOGIN_USER);
        Long totalCount= incomeRecordService.queryIncomeRecordCountByUserId(user.getId());
        incomePage.setTotalCount(totalCount);
        if(totalCount!=0 && cunPage>incomePage.getTotalPage()){
            cunPage=incomePage.getTotalPage();
        }
        incomePage.setCunPage(cunPage);
        HashMap<String,Object> map=new HashMap<>();
        map.put("userId",user.getId());
        map.put("start",(incomePage.getCunPage()-1)*incomePage.getPageSize());
        map.put("content",incomePage.getPageSize());
        List<IncomeRecord> incomeRecordList=incomeRecordService.queryIncomeRecordByUserId(map);
        model.addAttribute("incomeRecordList",incomeRecordList );
        return "myIncome";
    }
}
