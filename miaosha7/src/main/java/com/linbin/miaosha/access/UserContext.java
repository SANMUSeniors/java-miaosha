package com.linbin.miaosha.access;

import com.linbin.miaosha.domain.MiaoshaUser;

public class UserContext {
    private static  ThreadLocal<MiaoshaUser> useHolder = new ThreadLocal<MiaoshaUser>();
    public static void setUser(MiaoshaUser user)
    {
        useHolder.set(user);
    }
    public static MiaoshaUser getUser()
    {
        return useHolder.get();
    }
}
