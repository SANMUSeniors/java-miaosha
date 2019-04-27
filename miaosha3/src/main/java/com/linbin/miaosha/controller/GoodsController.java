package com.linbin.miaosha.controller;

import com.linbin.miaosha.domain.MiaoshaUser;
import com.linbin.miaosha.redis.RediService;
import com.linbin.miaosha.service.MiaoshaUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    MiaoshaUserService userService;

    @Autowired
    RediService redisService;

    @RequestMapping("/to_list")
    public String toLogin(@CookieValue(value = MiaoshaUserService.COOKI_NAME_TOKEN,required = false) String cookieToken,
                          @RequestParam(value = MiaoshaUserService.COOKI_NAME_TOKEN,required = false) String paramToken, HttpServletResponse response) {

        if(StringUtils.isEmpty(cookieToken)&&StringUtils.isEmpty( paramToken))
            return "login";
        String token =  StringUtils.isEmpty( paramToken)?cookieToken:paramToken;
        MiaoshaUser user = userService.getByToken( response,token);
        System.out.println(user);
        return "goods_list";
    }


}