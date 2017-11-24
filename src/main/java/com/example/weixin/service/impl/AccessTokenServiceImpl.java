package com.example.weixin.service.impl;

import com.example.weixin.entity.AccessToken;
import com.example.weixin.mapper.AccessTokenMapper;
import com.example.weixin.service.AccessTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by C on 2017/11/20.
 */
@Service
public class AccessTokenServiceImpl implements AccessTokenService{

    @Autowired
    private AccessTokenMapper accessTokenMapper;

    @Override
    public AccessToken getAccessToken() {
        return accessTokenMapper.getAccessToken();
    }

    @Override
    public void updateAccessToken(AccessToken accessToken) {
        accessTokenMapper.updateAccessToken(accessToken);
    }
}
