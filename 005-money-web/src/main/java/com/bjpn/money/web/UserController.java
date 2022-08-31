package com.bjpn.money.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.support.Parameter;
import com.alibaba.fastjson.JSONObject;
import com.bjpn.money.model.BidInfo;
import com.bjpn.money.model.IncomeRecord;
import com.bjpn.money.model.RechargeRecord;
import com.bjpn.money.model.User;
import com.bjpn.money.service.*;
import com.bjpn.money.utils.Constants;
import com.bjpn.money.utils.FastDFSClient;
import com.bjpn.money.utils.HttpClientUtils;
import com.bjpn.money.utils.Result;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jute.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Controller
public class UserController {
    @Reference(interfaceClass = UserService.class,version="1.0.0",timeout = 20000)
    private UserService userService;
    @Reference(interfaceClass = RedisService.class,version="1.0.0",timeout = 20000)
    private RedisService redisService;
    @Reference(interfaceClass = BidInfoService.class,version="1.0.0",timeout = 20000)
    private BidInfoService bidInfoService;
    @Reference(interfaceClass = RechargeRecordService.class,version="1.0.0",timeout = 20000)
    private RechargeRecordService rechargeRecordService;
    @Reference(interfaceClass = IncomeRecordService.class,version="1.0.0",timeout = 20000)
    private IncomeRecordService incomeRecordService;

    @GetMapping("/loan/page/register")
    public String register(String ReturnUrl,Model model){
        model.addAttribute("ReturnUrl",ReturnUrl);
        return "register";
    }

    @GetMapping("/loan/page/checkPhone")
    @ResponseBody
    public Object checkPhone(@RequestParam(name="phone",required = true) String phone){
        int num=userService.checkPhone(phone);
        if(num>0){
            return Result.error("该手机号已被注册");
        }
        return Result.success();
    }

    @PostMapping("/loan/page/registerSubmit")
    @ResponseBody
    public Object registerSubmit(@RequestParam(name="phone",required = true) String phone,
                                 @RequestParam(name="loginPassword",required = true) String loginPassword,
                                 @RequestParam(name="messageCode",required = true) String messageCode,
                                 HttpServletRequest request){
        String code=redisService.getCode(phone);
        if(code==null){
            return Result.error("请重新获取验证码！");
        }
        if(!StringUtils.equals(code,messageCode)){
            return Result.error("验证码输入错误！");
        }
        User user=userService.register(phone,loginPassword);
        if(!ObjectUtils.allNotNull(user)){
            return Result.error("注册失败！");
        }
        request.getSession().setAttribute(Constants.LOGIN_USER,user);
        return Result.success();
    }

    @GetMapping("/loan/page/messageCode")
    @ResponseBody
    public Object messageCode(@RequestParam(name="phone",required = true) String phone,
                              HttpServletRequest request){
        String code = Result.generateCode(4);
        HashMap<String,String> parseMap=new HashMap<>();
        parseMap.put("appkey", "099914fb85aee8854e5fcd2a179ac9dd");
        parseMap.put("mobile", phone);
        parseMap.put("content","【创信】你的验证码是："+code+"，3分钟内有效！" );
        String result="{\n" +
                "    \"code\": \"10000\",\n" +
                "    \"charge\": false,\n" +
                "    \"remain\": 1305,\n" +
                "    \"msg\": \"查询成功\",\n" +
                "    \"result\": {\n" +
                "        \"ReturnStatus\": \"Success\",\n" +
                "        \"Message\": \"ok\",\n" +
                "        \"RemainPoint\": 420842,\n" +
                "        \"TaskID\": 18424321,\n" +
                "        \"SuccessCounts\": 1\n" +
                "    }\n" +
                "}";
        try {
            //result=HttpClientUtils.doGet("https://way.jd.com/chuangxin/dxjk",parseMap);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("系统繁忙！");
        }
        JSONObject jsonObject=JSONObject.parseObject(result);
        String code1 = jsonObject.getString("code");
        if(!StringUtils.equals("10000",code1)){
            return Result.error("通讯异常！");
        }
        JSONObject jsonObject1=jsonObject.getJSONObject("result");
        String returnStatus = jsonObject1.getString("ReturnStatus");
        if(!StringUtils.equals("Success", returnStatus)){
            return Result.error("短信发送失败！");
        }

        //发送成功，存入redis
        redisService.push(phone,code);

        return Result.success(code);
    }


    @GetMapping("/loan/page/realName")
    public String realName(Model model,String ReturnUrl){
        model.addAttribute("ReturnUrl", ReturnUrl);
        return "realName";
    }

    @PostMapping("/loan/page/realNameSubmit")
    @ResponseBody
    public Object realNameSubmit(@RequestParam(name="phone",required = true) String phone,
                                 @RequestParam(name="realName",required = true) String realName,
                                 @RequestParam(name="idCard",required = true) String idCard,
                                 @RequestParam(name="messageCode",required = true) String messageCode,
                                 HttpServletRequest request){
        HashMap<String,String> parseMap=new HashMap<>();
        parseMap.put("appkey", "099914fb85aee8854e5fcd2a179ac9dd");
        parseMap.put("cardno", idCard);
        parseMap.put("name",realName);
        String result="{\n" +
                "    \"code\": \"10000\",\n" +
                "    \"charge\": false,\n" +
                "    \"remain\": 1305,\n" +
                "    \"msg\": \"查询成功\",\n" +
                "    \"result\": {\n" +
                "        \"resp\": {\n" +
                "            \"code\": 0,\n" +
                "            \"desc\": \"匹配\"\n" +
                "        },\n" +
                "        \"data\": {\n" +
                "            \"birthday\": \"1981-11-30\",\n" +
                "            \"sex\": \"M\",\n" +
                "            \"address\": \"山东省潍坊市寒亭区\"\n" +
                "        }\n" +
                "    }\n" +
                "}\n";
        try {
            //result=result=HttpClientUtils.doGet("https://way.jd.com/yingyan/idcard",parseMap);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("系统繁忙");
        }
        JSONObject jsonObject=JSONObject.parseObject(result);
        String code1 = jsonObject.getString("code");
        if(!StringUtils.equals(code1,"10000")){
            return Result.error("通讯异常");
        }
        int code2 = Integer.parseInt(jsonObject.getJSONObject("result").getJSONObject("resp").getString("code"));
        if(code2!=0){
            return Result.error("身份证号不匹配");
        }
        String code = redisService.getCode(phone);
        if(code==null){
            return Result.error("请获取验证码");
        }
        if(!StringUtils.equals(code,messageCode)){
            return Result.error("验证码输入错误");
        }
        User user=(User)request.getSession().getAttribute("loginUser");
        user.setName(realName);
        user.setIdCard(idCard);
        int num=userService.modifyUserByPhoneAndPsd(user);
        if(num==0){
            return Result.error("认证失败");
        }
        return Result.success();
    }

    @GetMapping("/loan/page/login")
    public String login(String ReturnUrl, Model model){
        model.addAttribute("ReturnUrl",ReturnUrl);
        return "login";
    }

    @PostMapping("/loan/page/loginSubmit")
    @ResponseBody
    public Object loginSubmit(@RequestParam(name="phone",required = true)String phone,
                              @RequestParam(name="loginPassword",required = true) String loginPassword,
                              HttpServletRequest request){
        User user=userService.login(phone,loginPassword);
        if(!ObjectUtils.allNotNull(user)){
            return Result.error("账号或密码错误");
        }
        request.getSession().setAttribute(Constants.LOGIN_USER,user);
        return Result.success();
    }

    @GetMapping("/loan/page/myCenter")
    public String myCenter(Model model,HttpServletRequest request){
        User user=(User)request.getSession().getAttribute(Constants.LOGIN_USER);
        if(user==null){
            return "redirect:/loan/page/login";
        }
        HashMap<String,Object> parseMap=new HashMap<>();
        parseMap.put("start",0);
        parseMap.put("content",5);
        parseMap.put("userId",user.getId());
        List<BidInfo> bidInfoList=bidInfoService.queryMyBidInfo(parseMap);
        List<RechargeRecord> rechargeRecordList=rechargeRecordService.queryMyRechargeRecord(parseMap);
        List<IncomeRecord> incomeRecordList=incomeRecordService.queryMyIncomeRecord(parseMap);
        model.addAttribute("bidInfoList", bidInfoList);
        model.addAttribute("rechargeRecordList",rechargeRecordList);
        model.addAttribute("incomeRecordList",incomeRecordList);
        return "myCenter";
    }

    @GetMapping("/loan/logout")
    public String logout(HttpServletRequest request){
        request.getSession().invalidate();
        return "redirect:/index";
    }

    /*@PostMapping("/loan/uploadHeader")
    public String uploadHeader(MultipartFile header,HttpServletRequest request){
        if (header.isEmpty()) {
            return "myCenter";
        }
        User user=(User)request.getSession().getAttribute(Constants.LOGIN_USER);
        String oldName=header.getOriginalFilename();
        String time=System.currentTimeMillis()+"";
        String fileName=time+oldName.substring(oldName.lastIndexOf("."));
        user.setHeaderImage(fileName);
        int num=userService.modifyUserForHeaderImage(user);
        String filePath = "F:/phase4_code/money/005-money-web/target/classes/static/headerImage/";
        File dest = new File(filePath + fileName);
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        try{
            header.transferTo(dest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "myCenter";

    }*/
    @PostMapping("/upload")
    public String uploadHeader(MultipartFile file,HttpServletRequest request,Model model){
        if (!file.isEmpty()) {
            User user = (User) request.getSession().getAttribute(Constants.LOGIN_USER);
            String oldName = file.getOriginalFilename();
            String url=null;
            if(oldName!=null) {
                try {
                    FastDFSClient fastDFSClient = new FastDFSClient("tracker.conf");
                    url=fastDFSClient.uploadFile(file.getBytes(), oldName.substring(oldName.lastIndexOf(".")+1));
                    System.out.println(url);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            user.setHeaderImage(url);
            int num = userService.modifyUserForHeaderImage(user);
            if(num!=1){
                model.addAttribute("errorMessage","头像上传失败");
            }
        }
        return "myCenter";
    }
}
