package com.example.weixin.mapper;

import com.example.weixin.entity.AccessToken;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created by C on 2017/11/20.
 */
@Mapper
public interface AccessTokenMapper {
    /**
     * 获取AccessToken
     * @return
     */
    AccessToken getAccessToken();

    /**
     * 更新AccessToken
     * @param accessToken
     */
    void updateAccessToken(AccessToken accessToken);
}
