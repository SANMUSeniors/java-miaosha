package com.linbin.miaosha.service;

import com.linbin.miaosha.Exception.GlobalException;
import com.linbin.miaosha.Result.CodeMsg;
import com.linbin.miaosha.Util.MD5Util;
import com.linbin.miaosha.Util.UUIDUtil;
import com.linbin.miaosha.dao.MiaoshaUserDao;
import com.linbin.miaosha.domain.MiaoshaUser;
import com.linbin.miaosha.redis.MiaoshaUserKey;
import com.linbin.miaosha.redis.RediService;
import com.linbin.miaosha.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class MiaoshaUserService {
    public static final String COOKI_NAME_TOKEN = "token";
    @Autowired
    MiaoshaUserDao miaoshaUserDao;
    @Autowired
    RediService rediService;
    public MiaoshaUser getById(long id) {
        //取缓存
        MiaoshaUser user = rediService.get(MiaoshaUserKey.getById, ""+id, MiaoshaUser.class);//10商品 ，2 req
        if(user != null) {
            return user;
        }
        //取数据库
        user = miaoshaUserDao.getById(id);
        if(user != null) {
            rediService.set(MiaoshaUserKey.getById, ""+id, user);
        }
        return user;
    }
    // http://blog.csdn.net/tTU1EvLDeLFq5btqiK/article/details/78693323
    public boolean updatePassword(String token, long id, String formPass) {
        //取user
        MiaoshaUser user = getById(id);
        if(user == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //更新数据库
        MiaoshaUser toBeUpdate = new MiaoshaUser();
        toBeUpdate.setId(id);
        toBeUpdate.setPassword(MD5Util.formPassToDBPass(formPass, user.getSalt()));
        miaoshaUserDao.update(toBeUpdate);
        //处理缓存
        rediService.delete(MiaoshaUserKey.getById, ""+id);
        user.setPassword(toBeUpdate.getPassword());
        rediService.set(MiaoshaUserKey.token, token, user);
        return true;
    }

    public String login(HttpServletResponse response, LoginVo loginVo) {
        if(loginVo == null) {
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();
//        System.out.println(formPass);
        //判断手机号是否存在
        MiaoshaUser user = getById(Long.parseLong(mobile));
        if(user == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //验证密码
        String dbPass = user.getPassword();
//        System.out.println(dbPass);
        String saltDB = user.getSalt();
//        System.out.println(saltDB);
        String calcPass = MD5Util.formPassToDBPass(formPass, saltDB);
//        System.out.println(calcPass);
        if(!calcPass.equals(dbPass)) {
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        //生成cookie
        String token	 = UUIDUtil.uuid();
        addCookie(response, token, user);
        return token;
    }

    private void addCookie(HttpServletResponse response, String token, MiaoshaUser user) {
        rediService.set(MiaoshaUserKey.token, token, user);
        Cookie cookie = new Cookie(COOKI_NAME_TOKEN, token);
        cookie.setMaxAge(MiaoshaUserKey.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public MiaoshaUser getByToken(HttpServletResponse response,String token) {
        if(StringUtils.isEmpty(token))
            return null;
       MiaoshaUser user= rediService.get(MiaoshaUserKey.token, token,MiaoshaUser.class);
       //延长有限期
        if(user != null) {
            addCookie(response, token, user);
        }
        return user;
    }
}
