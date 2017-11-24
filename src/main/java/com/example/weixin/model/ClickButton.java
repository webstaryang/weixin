package com.example.weixin.model;

/**
 * Created by C on 2017/11/22.
 */
public class ClickButton extends Button{
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    private String key;
}
