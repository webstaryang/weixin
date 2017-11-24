package com.example.weixin.service;

import com.example.weixin.entity.AccessToken;
import org.springframework.stereotype.Service;

/**
 * @author LY
 * Created by C on 2017/11/20.
 */
public interface AccessTokenService {

    AccessToken getAccessToken();

    void updateAccessToken(AccessToken accessToken);
}
