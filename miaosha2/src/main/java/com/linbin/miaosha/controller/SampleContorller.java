package com.linbin.miaosha.controller;

import com.linbin.miaosha.Result.Result;
import com.linbin.miaosha.domain.User;
import com.linbin.miaosha.redis.RediService;
import com.linbin.miaosha.redis.UserKey;
import com.linbin.miaosha.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;



@Controller
@RequestMapping("/demo")
public class  SampleContorller {
    @Autowired
    UserService userService;
    @Autowired
    RediService rediService;
    @RequestMapping("/thymeleaf")
    @ResponseBody
    public  String thymeleaf( Model model)
    {
        model.addAttribute("name","linbin");
        return "helloaa";
    }
    @RequestMapping("/db/get")
    @ResponseBody
    public Result<User> doGet()
    {
        User user = userService.getById(1);
        return Result.success(user);
    }
    @RequestMapping("/db/tx")
    @ResponseBody
    public Result<Boolean> dbtx()
    {
        userService.tx();
        return Result.success(true);
    }

    @RequestMapping("/redis/get")
    @ResponseBody
    public Result<User> redisGet()

    {
        User  user  = rediService.get(UserKey.getById, ""+1, User.class);
        return Result.success(user);
    }
    @RequestMapping("/redis/set")
    @ResponseBody
    public Result<Boolean> redisSet() {
        User user  = new User();
        user.setId(1);
        user.setName("1111");
        rediService.set(UserKey.getById, ""+1, user);//UserKey:id1
        return Result.success(true);
    }

}
