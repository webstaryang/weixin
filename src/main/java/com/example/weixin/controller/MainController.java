package com.example.weixin.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.weixin.entity.AccessToken;
import com.example.weixin.http.HttpClientUtil;
import com.example.weixin.http.HttpResult;
import com.example.weixin.model.*;
import com.example.weixin.service.AccessTokenService;
import com.example.weixin.util.AccessTokenUtil;
import com.example.weixin.util.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

/**
 * Created by C on 2017/11/15.
 */
@Controller
public class MainController {

    @Autowired
    private AccessTokenService accessTokenService;

    @RequestMapping(value = "/weixin", method = RequestMethod.GET)
    @ResponseBody
    public String Check(HttpServletRequest request){
        String signature = request.getParameter("signature");
        String token = "gohigher";
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        String echostr = request.getParameter("echostr");
        String[] strs = {token,timestamp,nonce};
        Arrays.sort(strs);
        String strSha1 = getSha1(strs);
        if( strSha1.equals(signature)){
            System.out.println("接入成功");
            return echostr;
        }
        return "null";
    }

    public static String getSha1(String[] strs){
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<strs.length;i++){
            sb.append(strs[i]);
        }
        String str = sb.toString();
        if( str==null || str.length()==0 ) {
            return null;
        }
        char hexDigits[] = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
            mdTemp.update(str.getBytes("UTF-8"));

            byte[] md = mdTemp.digest();
            int j = md.length;
            char buf[] = new char[j*2];
            int k =0;
            for (int i=0;i<j;i++) {
                byte byte0 = md[i];
                buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
                buf[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(buf);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String talk(String question){

        String key = "db4bdfa4d28940d4992af280989fe8f6";
        String userid = "liuyang123";
        RobotRequestModel rrm = new RobotRequestModel();
        rrm.setKey(key);
        rrm.setInfo(question);
        rrm.setUserid(userid);
        return JSON.toJSONString(rrm);
    }

    @RequestMapping(value = "/weixin", method = RequestMethod.POST)
    public void getText(HttpServletRequest req, HttpServletResponse resp) throws IOException{

        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        try {
            Map<String,String> map = MessageUtil.xmlToMap(req);
            String fromUserName = map.get("FromUserName");
            String toUserName = map.get("ToUserName");
            String msgType = map.get("MsgType");
            String content = map.get("Content");
            String msgId = map.get("MsgId");

            String url = "http://www.tuling123.com/openapi/api";
            HttpResult result = HttpClientUtil.post(url,talk(content));

            String message = null;
            if(MessageUtil.MESSAGE_TEXT.equals(msgType)){
                TextMessage text = new TextMessage();
                text.setFromUserName(toUserName);
                text.setToUserName(fromUserName);
                text.setMsgType(MessageUtil.MESSAGE_TEXT);
                text.setCreateTime(new Date().getTime()+"");
                JSONObject jsonObject = JSONObject.parseObject(result.getContent());
                String resultMessage = jsonObject.getString("text");
                text.setContent(resultMessage);
                text.setMsgId(msgId);
                message = MessageUtil.textMessageToXml(text);
            }else if(MessageUtil.MESSAGE_EVENT.equals(msgType)){
                String eventType = map.get("Event");
                if(MessageUtil.MESSAGE_SUBSCRIBE.equals(eventType)){
                    message = MessageUtil.initText(toUserName,fromUserName,MessageUtil.menuText());
                }
            }
            out.print(message);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.close();
        }
    }

    @RequestMapping("/createMenu")
    @ResponseBody
    public String createMenu() throws IOException, GeneralSecurityException {
        AccessToken accessToken = accessTokenService.getAccessToken();
        Date old_time = accessToken.getUpdateTime();
        if ((new Date().getTime()-old_time.getTime())>(accessToken.getExpires()*1000)) {
            AccessToken new_accessToken = AccessTokenUtil.getAccessTokenFromWx();
            accessTokenService.updateAccessToken(new_accessToken);
        }

        String url = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token="+accessToken.getAccessToken();

        Menu menu = new Menu();
        ClickButton button1 = new ClickButton();
        button1.setName("测试1");
        button1.setType("click");
        button1.setKey("111111");

        Button button2 = new Button();
        button2.setName("测试2");
        ClickButton button3 = new ClickButton();
        button3.setName("测试3");
        button3.setType("click");
        button3.setKey("333333");
        ClickButton button4 = new ClickButton();
        button4.setName("测试4");
        button4.setType("click");
        button4.setKey("444444");
        button2.setSub_button(new Button[]{button3,button4});
        menu.setButton(new Button[]{button1,button2});

        String menuString = JSONObject.toJSONString(menu);
        HttpResult result = HttpClientUtil.post(url,menuString);
        if(!result.isError()){
            return "create success";
        }else{
            return "create false";
        }
    }

//    @RequestMapping(value = "/weixin", method = RequestMethod.POST)
//    public String getText(HttpServletRequest req){
//        System.out.println("测试");
//
//        try {
//            Map<String,String> map = MessageUtil.xmlToMap(req);
//            String fromUserName = map.get("FromUserName");
//            String toUserName = map.get("ToUserName");
//            String msgType = map.get("MsgType");
//            String content = map.get("Content");
//            String msgId = map.get("MsgId");
//
//            String message = null;
//            if("text".equals(msgType)){
//                TextMessage text = new TextMessage();
//                text.setFromUserName(toUserName);
//                text.setToUserName(fromUserName);
//                text.setMsgType("text");
//                text.setCreateTime(new Date().getTime()+"");
//                text.setContent("您发送的消息是："+content);
//                text.setMsgId(msgId);
//                message = MessageUtil.textMessageToXml(text);
//                System.out.println(message);
//                return message;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
}
