package com.linbin.miaosha.controller;

import com.linbin.miaosha.Result.Result;

import com.linbin.miaosha.redis.RediService;
import com.linbin.miaosha.service.MiaoshaUserService;
import com.linbin.miaosha.vo.LoginVo;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.slf4j.Logger;


import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;


@Controller
@RequestMapping("/login")
public class LoginController {

    private static Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    MiaoshaUserService userService;

    @Autowired
    RediService redisService;

    @RequestMapping("/to_login")
    public String toLogin() {
        return "login";
    }

    @RequestMapping("/do_login")
    @ResponseBody
    public Result<Boolean> doLogin(HttpServletResponse response,@Valid LoginVo loginVo) {//JSR303校验
        log.info(loginVo.toString());
        //登录
//        //参数校验
//        String passInput = loginVo.getPassword();
//        String mobile = loginVo.getMobile();
//        if(StringUtils.isEmpty(passInput))
//            return Result.error(CodeMsg.PASSWORD_EMPTY);
//        if(StringUtils.isEmpty(mobile))
//            return Result.error(CodeMsg.MOBILE_EMPTY);
//        if(!ValidatorUtil.isMobile(mobile))
//            return Result.error(CodeMsg.MOBILE_ERROR);
        //登录
         userService.login(response,loginVo);
         return Result.success(true);
    }
}