package com.linbin.miaosha.service;

import com.linbin.miaosha.Result.CodeMsg;
import com.linbin.miaosha.Util.MD5Util;
import com.linbin.miaosha.dao.MiaoshaUserDao;
import com.linbin.miaosha.domain.MiaoshaUser;
import com.linbin.miaosha.domain.User;
import com.linbin.miaosha.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MiaoshaUserService {
    @Autowired
    MiaoshaUserDao miaoshaUserDao;
    public MiaoshaUser getById(long id)
    {
        return miaoshaUserDao.getByid(id);
    }

    public CodeMsg login(LoginVo loginVo) {
        if (loginVo==null)
            return CodeMsg.SERVER_ERROR;
        String mobile = loginVo.getMobile();
        String formpass = loginVo.getPassword();
        //判断手机号是否存在
      MiaoshaUser user  = getById(Long.parseLong(mobile));
      if(user==null)
          return CodeMsg.MOBILE_NOT_EXIST;
        //验证密码
        String dbPass = user.getPassword();
        String saltDb = user.getSalt();
        String calcPass= MD5Util.formPassToDBPass(formpass,saltDb);
       if(!calcPass.equals(dbPass))
       {
           return CodeMsg.PASSWORD_ERROR;
       }

      return CodeMsg.SUCCESS;
    }
}
