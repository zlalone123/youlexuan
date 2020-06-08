package com.zl.utils;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;

public class SendSmsUtil {



    public static String sendCode(String phone,String code) {
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAI4Fkz7RkcTfdPrqfJqVUB", "pTw0oytByNTepTZIa0eGTwRCd1s2F9");
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", phone);
        request.putQueryParameter("SignName", "优乐选");
        request.putQueryParameter("TemplateCode", "SMS_174989901");
        request.putQueryParameter("TemplateParam", "{\"code\":\""+code+"\"}");
        String str = "";
        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
            str = response.getData();
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static void main(String[] args) {
        sendCode("13152255020","123456");
    }

}
