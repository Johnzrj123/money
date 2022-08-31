package com.bjpn.money.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.bjpn.money.model.BidInfo;
import com.bjpn.money.model.LoanInfo;
import com.bjpn.money.model.PageModel;
import com.bjpn.money.service.BidInfoService;
import com.bjpn.money.service.LoanInfoService;
import com.bjpn.money.utils.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
public class LoanController {
    @Reference(interfaceClass = LoanInfoService.class,version="1.0.0",timeout = 20000)
    private LoanInfoService loanInfoService;
    @Reference(interfaceClass = BidInfoService.class,version="1.0.0",timeout = 20000)
    private BidInfoService bidInfoService;
    @GetMapping("/loan/loan")
    public String loan(/*@RequestParam(name="pageNum",required = true) String pageNum,*/
            Long cunPage, @RequestParam(name="ptype",required = true) Integer ptype,
            Model model, HttpServletRequest request){
        /*Long loanInfoCount=loanInfoService.queryLoanInfoCount(ptype);
        Page page=new Page(loanInfoCount,pageNum);
        //产品展现
        HashMap<String,Object> parseMap=new HashMap<>();
        parseMap.put("ptype", ptype);
        parseMap.put("start",page.getStartIndex());
        parseMap.put("content",page.getContent());
        List<LoanInfo> loanInfoList=loanInfoService.queryLoanInfoByTypeAndNum(parseMap);
        model.addAttribute("ptype",ptype);
        model.addAttribute("page",page);
        model.addAttribute("loanInfoList", loanInfoList);*/

        PageModel pageModel=(PageModel)request.getSession().getAttribute("pageModel");

        if(pageModel==null){
            pageModel=new PageModel(9);
            request.getSession().setAttribute("pageModel",pageModel);
        }
        if(cunPage==null || cunPage<1){
           cunPage=pageModel.getFirstPage().longValue();
        }

        //todo:在redis中存放部分数据，一旦超出界限，直接访问redis设置好的数据，降低数据库压力
        //设置总记录数
        Long totalCount = loanInfoService.queryLoanInfoCount(ptype);
        pageModel.setTotalCount(totalCount);
        //客户端验证，超出尾页就默认访问尾页
        if(pageModel.getTotalPage()<cunPage){
            cunPage=pageModel.getTotalPage();
        }
        //设置当前页
        pageModel.setCunPage(cunPage);


        List<LoanInfo> loanInfoList=loanInfoService.queryLoanInfoByTypeAndPageMode(ptype,pageModel);
        model.addAttribute("loanInfoList",loanInfoList);
        model.addAttribute("ptype",ptype);
        return "loan";
    }

    @GetMapping("/loan/loanInfo")
    public String loanInfo(@RequestParam(name="loanId",required = true)Integer loanId,
                           Long cunPage,Model model,HttpServletRequest request){
        //查询产品信息
        LoanInfo loanInfo=loanInfoService.queryLoanInfoById(loanId);
        model.addAttribute("loanInfo",loanInfo);
        //产品投资记录    分页
        PageModel pageModel1=(PageModel)request.getSession().getAttribute("pageModel1");
        if(pageModel1==null){
            pageModel1=new PageModel(5);
            request.getSession().setAttribute("pageModel1",pageModel1);
        }
        if(cunPage==null || cunPage<1){
            cunPage=pageModel1.getFirstPage().longValue();
        }
        Long totalCount = bidInfoService.queryBidInfoCount(loanId);
        pageModel1.setTotalCount(totalCount);
        if(totalCount!=0 && pageModel1.getTotalPage()<cunPage){
            cunPage=pageModel1.getTotalPage();
        }
        pageModel1.setCunPage(cunPage);
        ArrayList<BidInfo> bidInfoList=bidInfoService.queryBidInfoByLoanId(loanId,pageModel1);
        model.addAttribute("bidInfoList",bidInfoList);
        model.addAttribute("loanId",loanId );
        //todo:投资排行榜
        String bidRankingList=bidInfoService.bidRanking();
        return "loanInfo";
    }

    @GetMapping("/loan/bidInfoList")
    public Object loanInfoList(@RequestParam(name="loanId",required = true)Integer loanId,
                           Long cunPage,Model model,HttpServletRequest request){
        //产品投资记录    分页
        PageModel pageModel1=(PageModel)request.getSession().getAttribute("pageModel1");
        /*if(cunPage==null || pageModel.getFirstPage()>cunPage){
            cunPage=pageModel.getFirstPage().longValue();
        }
        Long totalCount = bidInfoService.queryBidInfoCount(loanId);
        pageModel.setTotalCount(totalCount);
        if(pageModel.getTotalPage()<cunPage){
            cunPage=pageModel.getTotalPage();
        }*/
        pageModel1.setCunPage(cunPage);
        ArrayList<BidInfo> bidInfoList=bidInfoService.queryBidInfoByLoanId(loanId,pageModel1);
        model.addAttribute("loanId",loanId);
        model.addAttribute("bidInfoList", bidInfoList);
        return "loanInfo::get-response";
    }
}
