package com.linbin.miaosha.redis;
//用到模板模式
public interface KeyPrefix {
    public int expireSeconds();//有效期。
    public String getPrefix();
}
