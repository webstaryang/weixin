package com.example.weixin;

import com.example.weixin.entity.AccessToken;
import com.example.weixin.service.AccessTokenService;
import com.example.weixin.service.impl.AccessTokenServiceImpl;
import com.example.weixin.util.AccessTokenUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WeixinApplicationTests {

	@Autowired
	private AccessTokenService access;

	@Test
	public void contextLoads() {
	}

	@Test
	public void testGetAccessToken(){
		AccessToken accessToken = access.getAccessToken();
		System.out.println(accessToken.getUpdateTime());
		AccessToken accessToken1 = new AccessToken();
		accessToken1.setAccessToken("444");
		accessToken1.setExpires(500);
		accessToken1.setUpdateTime(new Date());
		access.updateAccessToken(accessToken1);
	}

	@Test
	public void testTokenUtil() throws IOException, GeneralSecurityException {
		AccessToken accessToken = new AccessTokenUtil().getAccessTokenFromWx();
		access.updateAccessToken(accessToken);
	}
}
