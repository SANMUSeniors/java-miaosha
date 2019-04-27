package com.linbin.miaosha.access;

import com.alibaba.fastjson.JSON;
import com.linbin.miaosha.Result.CodeMsg;
import com.linbin.miaosha.Result.Result;
import com.linbin.miaosha.domain.MiaoshaUser;
import com.linbin.miaosha.redis.AccessKey;
import com.linbin.miaosha.redis.RediService;
import com.linbin.miaosha.service.MiaoshaUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.logging.Handler;

@Service
public class AccessInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    MiaoshaUserService userService;

    @Autowired
    RediService redisService;
    @Override
   public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        MiaoshaUser user =getUser(request,response);
        UserContext.setUser(user);
        if(handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler;
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            if (accessLimit == null)
                return true;
            int seconds = accessLimit.seconds();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();
            String key = request.getRequestURI();
            if (needLogin) {
                if (user == null) {
                    renger(response, CodeMsg.SESSION_ERROR);
                    return false;
                }
                key += "_" + user.getId();
            } else {
                //donothing
            }
            //重构-改善既有代码的设计
            AccessKey ak = AccessKey.withExpire(seconds);
            Integer count = redisService.get(ak, key, Integer.class);
            if (count == null)
                redisService.set(ak, key, 1);
            else if (count < maxCount)
                redisService.incr(ak, key);
            else {
                renger(response,CodeMsg.ACCESS_LIMIT_REACHED);
                return false;
            }

        }
        return true;
    }
    private void renger(HttpServletResponse response, CodeMsg cm) throws Exception{
        response.setContentType("application/json;charset=UTE-8");
        OutputStream out=response.getOutputStream();
        String str=JSON.toJSONString(Result.error(cm));
        out.write(str.getBytes("UTF-8"));
        out.flush();
        out.close();
    }


    private MiaoshaUser getUser(HttpServletRequest request, HttpServletResponse response)
    {
       ;

        String paramToken = request.getParameter(MiaoshaUserService.COOKI_NAME_TOKEN);
        String cookieToken = getCookieValue(request, MiaoshaUserService.COOKI_NAME_TOKEN);
        if(StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
            return null;
        }
        String token = StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
        return userService.getByToken(response, token);
    }

    private String getCookieValue(HttpServletRequest request, String cookiName) {
        Cookie[]  cookies = request.getCookies();
        if(cookies == null || cookies.length <= 0){
            return null;
        }
        for(Cookie cookie : cookies) {
            if(cookie.getName().equals(cookiName)) {
                return cookie.getValue();
            }
        }
        return null;
    }
}