package com.bjpn.money.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.internal.util.AlipaySignature;
import com.bjpn.money.config.AlipayConfig;
import com.bjpn.money.model.PageModel;
import com.bjpn.money.model.RechargeRecord;
import com.bjpn.money.model.User;
import com.bjpn.money.service.RechargeRecordService;
import com.bjpn.money.utils.Constants;

import com.bjpn.money.utils.HttpClientUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


@Controller
public class RechargeController {
    @Reference(interfaceClass = RechargeRecordService.class,version="1.0.0",timeout = 20000)
    private RechargeRecordService rechargeRecordService;

    @GetMapping("/loan/page/toRecharge")
    public String toReCharge(){
        return "toRecharge";
    }

    @RequestMapping("/loan/page/payBack")
    public String payBack(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //获取支付宝GET过来反馈信息
        Map<String,String> params = new HashMap<String,String>();
        Map<String,String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }
        //调用SDK验证签名
        boolean signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.sign_type);

        //——请在这里编写您的程序（以下代码仅作参考）——
        if(signVerified) {
            //商户订单号
            String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");

            //支付宝交易号
            String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");

            //付款金额
            String total_amount = new String(request.getParameter("total_amount").getBytes("ISO-8859-1"),"UTF-8");

            response.getWriter().println("trade_no:"+trade_no+"<br/>out_trade_no:"+out_trade_no+"<br/>total_amount:"+total_amount);

            //采用乐观锁解决重复充值问题
            String result="{\n" +
                    "  \"alipay_trade_query_response\": {\n" +
                    "    \"code\": \"10000\",\n" +
                    "    \"msg\": \"Success\",\n" +
                    "    \"buyer_logon_id\": \"qqf***@sandbox.com\",\n" +
                    "    \"buyer_pay_amount\": \"0.00\",\n" +
                    "    \"buyer_user_id\": \"2088622987351225\",\n" +
                    "    \"buyer_user_type\": \"PRIVATE\",\n" +
                    "    \"invoice_amount\": \"0.00\",\n" +
                    "    \"out_trade_no\": \"2022072010572016760772\",\n" +
                    "    \"point_amount\": \"0.00\",\n" +
                    "    \"receipt_amount\": \"0.00\",\n" +
                    "    \"send_pay_date\": \"2022-07-20 10:57:52\",\n" +
                    "    \"total_amount\": \"100.00\",\n" +
                    "    \"trade_no\": \"2022072022001451220502508484\",\n" +
                    "    \"trade_status\": \"TRADE_SUCCESS\"\n" +
                    "  },\n" +
                    "  \"sign\": \"W6fbuSK5g1UJKD88L1rxJ9XIpZoOcM5o9NYIicZHRRJmNUKP1Xqc9g/N9BohVS8JKdUbqoIPt0rAmUJXAB0EJnLWGKdMdXJIuqoDses+17rBjMT0rijbD7cqRX+meaeWHjeMJhWbflVfce8SsO47hx0bkB3s2ANopOhfcl3tT7kL8Hz4hKRrERD6pNUd79ZI2RtCHisBy+wM5mV36lxzmJGxksA9RHubn2M1HN+Q9iUBSCvj8yLHfeC4DM6GlGlkGnOUzQSRKIBFmJc764ku1WkNU8qJto+00/PJl2MUBgrMLwGEF1pJtb/ss+y5DKHZFE4SwehxKBMmV1B4XndAUg==\"\n" +
                    "}";
            //查询订单交易状态
            result=HttpClientUtils.doGet("http://localhost:9007/007-money-pay//loan/pay/queryOrder?out_trade_no="+out_trade_no);
            System.out.println(result);
            JSONObject jsonObject=JSONObject.parseObject(result).getJSONObject("alipay_trade_query_response");
            if(!StringUtils.equals(jsonObject.getString("code"),"10000")){
                //...
            }
            if(StringUtils.equals("WAIT_BUYER_PAY",jsonObject.getString("trade_status"))){
                //todo:发短信
            }
            if(StringUtils.equals("TRADE_CLOSED",jsonObject.getString("trade_status"))){
                //修改订单状态为2，响应失败信息
                int num=rechargeRecordService.rechargeFail(out_trade_no);
                if(num!=1){
                    //...
                }
                //发短信、返回小金库
                return "redirect:/loan/page/myCenter";
            }
            if(StringUtils.equals("TRADE_SUCCESS",jsonObject.getString("trade_status"))){
                HashMap<String,Object> parseMap=new HashMap<>();
                parseMap.put("out_trade_no",out_trade_no);
                parseMap.put("total_amount",Double.parseDouble(total_amount));
                //是否登录
                User user=(User)request.getSession().getAttribute(Constants.LOGIN_USER);
                if(ObjectUtils.allNotNull(user)){
                    parseMap.put("userId",user.getId());
                }
                //修改订单状态为1，余额增加，响应成功信息
                int num=rechargeRecordService.rechargeSuccess(parseMap);
                if(num!=1){
                    //发短信：预计10分钟到账
                }
                return "redirect:/loan/page/myCenter";
            }
        }else {
            response.getWriter().println("验签失败");
        }

        return null;
    }

    @PostMapping("/loan/page/aliPay")
    public String aliPay(@RequestParam(name="rechargeMoney",required = true)Double rechargeMoney,
                         HttpServletRequest request,
                         Model model){
        User user=(User)request.getSession().getAttribute(Constants.LOGIN_USER);
        //todo:是否登录
        //todo:是否实名认证
        //todo:验证金额为数字
        //生成订单
        RechargeRecord rechargeRecord=rechargeRecordService.recharge(user.getId(),rechargeMoney);
        if (rechargeRecord==null){
            model.addAttribute("trade_msg","订单生成失败" );
            return "toRechargeBack";
        }
        //支付
        model.addAttribute("rechargeRecord",rechargeRecord);
        //return "redirect:http://localhost:9007/007-money-pay/loan/pay/aliPay?out_trade_no=123123&total_amount=123&subject=test&body=test";
        return "toAliPay";
    }

    @PostMapping("/loan/page/wxPay")
    public String wxPay(@RequestParam(name="rechargeMoney",required = true)Double rechargeMoney){
        return "toRecharge";
    }

    @GetMapping("/loan/myRecharge")
    public String myRecharge(Long cunPage,HttpServletRequest request,Model model){
        PageModel rechargePage=(PageModel)request.getSession().getAttribute("rechargePage");
        if(rechargePage==null){
            rechargePage=new PageModel(6);
            request.getSession().setAttribute("rechargePage",rechargePage);
        }
        if(cunPage==null || cunPage<1){
            cunPage=rechargePage.getFirstPage().longValue();
        }
        User user=(User)request.getSession().getAttribute(Constants.LOGIN_USER);
        Long totalCount=rechargeRecordService.queryRechargeRecordCountByUserId(user.getId());
        rechargePage.setTotalCount(totalCount);
        if(totalCount!=0 && cunPage>rechargePage.getTotalPage()){
            cunPage=rechargePage.getTotalPage();
        }
        rechargePage.setCunPage(cunPage);
        HashMap<String,Object> map=new HashMap<>();
        map.put("userId",user.getId());
        map.put("start",(cunPage-1)*rechargePage.getPageSize());
        map.put("content",rechargePage.getPageSize());
        List<RechargeRecord> rechargeRecordList=rechargeRecordService.queryRechargeRecordByUserId(map);
        model.addAttribute("rechargeRecordList", rechargeRecordList);
        return "myRecharge";
    }
}
