package com.bjpn.money.utils;

import java.util.HashMap;
import java.util.Map;

public class Result {
    //{"code":1,"message":"","success":true}
    public static Map<String,Object> success(){
        Map<String,Object> map=new HashMap<>();
        map.put("code",1);
        map.put("message","");
        map.put("success",true);
        return map;
    }

    public static Map<String,Object> success(String message){
        Map<String,Object> map=new HashMap<>();
        map.put("code",1 );
        map.put("message", message);
        map.put("success",true);
        return map;
    }

    public static Map<String,Object> error(){
        Map<String,Object> map=new HashMap<>();
        map.put("code",0);
        map.put("message","");
        map.put("success",false);
        return map;
    }

    public static Map<String,Object> error(String message){
        Map<String,Object> map=new HashMap<>();
        map.put("code",0 );
        map.put("message", message);
        map.put("success",false);
        return map;
    }

    public static String generateCode(int len){
        StringBuilder stringBuilder=new StringBuilder();
        for (int i = 0; i < len; i++) {
            stringBuilder.append(Math.round(Math.random()*9));
        }
        return stringBuilder.toString();
    }
}
