package com.bjpn.money.config;

import java.io.FileWriter;
import java.io.IOException;

public class AlipayConfig {
    //↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    public static String app_id = "2021000121627218";

    // 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC8827VT9D7RvZsrZbFJ4feXL11qRkTFHJC4L9Wx/ofCvz8XczofMWP2u2xSkkZvrG68ngrzyGsB+Xw6+tUqd9eZ95uBNFY9W/GSjNdvLrw/pFqsWrJu48gSpHnZ+i9aZyVbkcgmWo8Z4Ua+wm1Wdkq+rq+X9ojSiYbpeAa02AHi1M5oLpW2qdOHSAD8TS/MO0/voNyqBZIfRXbKa205mfkw1aic7BvCM0SpLEmeH36Mbsiz3AidWajyekYVfMkhPuw1gHh416Q24ii7UVEYJMyk4fhauVVbHOUsNxO2Lg+ttJa8i2pEfgBoaEfg5HOtyZQPYIB9kKXjE9b/Ww7mM/pAgMBAAECggEBAK5jZlmNju07M1heZhaUQ3vg0CN16+SGLGa5Ex8+9gQMmi05TVMNRTFd9553VzxhmZtXCb41L3ic2YYf11SOa8+vwcR8wdJ+w6iY9wt+knK2IPP9xvdlGdXNvRd7+XxHqvLnktHthfU1/c1HAFDWGe7m9c+am7OSTdWapFld2PVb8/4UHY0/08beZ/3u819J6bQtYdGvnDSpQpKXQSx/90me8S7G/glgaP2wY7K6chv0WXn2RH1tbgukAlADPOwr1zPILqht0n6j93bs5SpGO4AUV6KdmQu9nAs+W11Gx9VyMvVRPhoqrUGw4JicU45kBT5vwHGGGijbIhoZlbJAKwECgYEA75+rp73/JG++k/WyCKX3FiSwXDRebURgNzj37/x7g9aRqq/yr2WtqEL2iKwMdF0aE7cXIsT06INg3K+GXYavY9FYMh4Ox6ys5nuepezlLm+MjiNE3Cba08oj1IHRqYxaEXfnusR3J0VPTpUSd9ItoEHziwOqsVdY5ArRElA+rHECgYEAyd03yPkllywzvLOEc2vyWeCkzyvcwuzybA4amXMrwMaqb2Q6M4b/J9UvXe/ckrc/IKs3Gj9vdrt56RX5/1gQgrzzB6NoiGF5yH7DemnYOkjHnAiiIXyv+tFWg8kxQg9bKzktbXyLRsijImU1uG95P1kA01NsTU75kSHoRR0XdvkCgYAui3tSV+uJblM8dksgtuwp1vKS1nFJFDEESQKQESEE2NbuqsKfI0b6Ghe9MH4y1CrOjY/m5w8TYwGbnFSjMhg7cVyKT6BbVMU49+Y3IK7buDs7GMq1YK682NTg4F6TannMTtgqRSNH84SWJQqzT5YhPO5LoGi1jdvsX9WAkym5YQKBgQDE5umhOZV3LgCRUjIiO+s7zCo2grxlzqZwzBkaEQNemOpqmUNEO6vreMEpSezHl8MV5KxtB+97y0cRGwSuE4KXZCNTEgvG6/3UmgGnCFOIFrHN+4XaUDt/1m4jQj6wBzaQpMcxAskAFHk79ak+7E1Cg1iQydDcX+qDrKVZtRTMUQKBgHC4UMDNL3dmL9Upt0i0MErE5BtcrFUqCoIHnC3dXSyPg0uvtCMdJLUONrCxdoZfWDodBt9NA6iqE8B6Zlhq6PQiASpBCY2QjXb0FCmZe2AFz+/8ZbrfOmfoj/cjcm7jCTDrX1oZBGsYtLQiK4/3GwWjBhLiGlC45MmTwnI0v9Nc";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiitPnZp0IXenyG8qOI0Tps1VKXcdQ9OzKqeazr/5JDthA5uSCxLZYONvNyr8E9XuUMaEPfPcwIEUmbiHiiFauXMbVc7NjpQaS0/XnMcywzv8oITfUH3HDYq0wA1RDcZZW4DIVPv8Wty/OBvjO13AMw0JhpHmyQhgvZPpT0B7YPR0Rb2HdhyhdA5NOlDX/IwgdmIVNKM7vO7hj4w54vXPhuYoz059Nt2MLr2sKlrDzulyc8Qr+cVtdCUgHfwAs+hzJYJusRd90ukMB8E5wCWun+ZLp2YkzkMTPVVk18j9Wz43m5PKMUGSpJ31C5gvqwuueE8mC2aDZgjPLg4JRPoFlQIDAQAB";

    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String notify_url = "http://localhost:8080/alipay.trade.page.pay-JAVA-UTF-8/notify_url.jsp";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String return_url = "http://localhost:9005/005-money-web/loan/page/payBack";

    // 签名方式
    public static String sign_type = "RSA2";

    // 字符编码格式
    public static String charset = "utf-8";

    // 支付宝网关
    public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    // 支付宝网关
    public static String log_path = "C:\\";


//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

    /**
     * 写日志，方便测试（看网站需求，也可以改成把记录存入数据库）
     * @param sWord 要写入日志里的文本内容
     */
    public static void logResult(String sWord) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(log_path + "alipay_log_" + System.currentTimeMillis()+".txt");
            writer.write(sWord);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
