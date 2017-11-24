package com.example.weixin.util;

import com.alibaba.fastjson.JSONObject;
import com.example.weixin.entity.AccessToken;
import com.example.weixin.http.HttpClientUtil;
import com.example.weixin.http.HttpResult;
import com.example.weixin.service.AccessTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.Properties;

/**
 * Created by C on 2017/11/17.
 */
public class AccessTokenUtil {

    private static String grant_type;
    private static String appid;
    private static String secret;
    static{
        try {
            InputStream in = AccessTokenUtil.class.getClass().getResourceAsStream("/application.properties");
            Properties p = new Properties();
            p.load(in);
            grant_type = p.getProperty("weixin.grant_type");
            appid = p.getProperty("weixin.appid");
            secret = p.getProperty("weixin.secret");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Resource(name="accessTokenService")
    private static AccessTokenService accessTokenService;

    public static AccessToken getAccessTokenFromWx() throws IOException, GeneralSecurityException{
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type="+grant_type+"&appid="+appid+"&secret="+secret;
        HttpResult result = HttpClientUtil.get(url);
        AccessToken accessToken = null;
        if (!result.isError()) {
            accessToken = new AccessToken();
            String content = result.getContent();
            JSONObject jsonObject = JSONObject.parseObject(content);
            String access_token = jsonObject.getString("access_token");
            accessToken.setAccessToken(access_token);
            Integer expires = Integer.valueOf(jsonObject.getString("expires_in"));
            accessToken.setExpires(expires);
            Date date = new Date();
            accessToken.setUpdateTime(date);
        }
        return accessToken;
    }

    public static boolean tokenIsOverdue(){
        AccessToken accessToken = accessTokenService.getAccessToken();
        long oldTime = accessToken.getUpdateTime().getTime();
        Date date = new Date();
        long newTime = date.getTime();
        if ((newTime-oldTime)>accessToken.getExpires())
            return true;
        else
            return false;
    }
}
