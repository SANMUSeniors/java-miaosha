package com.linbin.miaosha.controller;

import com.linbin.miaosha.Result.CodeMsg;
import com.linbin.miaosha.Result.Result;
import com.linbin.miaosha.domain.MiaoshaOrder;
import com.linbin.miaosha.domain.MiaoshaUser;
import com.linbin.miaosha.domain.OrderInfo;
import com.linbin.miaosha.redis.RediService;
import com.linbin.miaosha.service.GoodsService;
import com.linbin.miaosha.service.MiaoshaService;
import com.linbin.miaosha.service.MiaoshaUserService;
import com.linbin.miaosha.service.OrderService;
import com.linbin.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController {

    @Autowired
    MiaoshaUserService userService;

    @Autowired
    RediService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;

    /**
     * QPS:1306
     * 5000 * 10
     * */
    /**
     *  GET POST有什么区别？
     * */
    @RequestMapping(value="/do_miaosha", method= RequestMethod.POST)
    @ResponseBody
    public Result<OrderInfo> miaosha(Model model,MiaoshaUser user,
                                     @RequestParam("goodsId")long goodsId)
    {

        System.out.println("goodsId: "+goodsId);
        model.addAttribute("user", user);
        if(user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        //判断库存
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);//10个商品，req1 req2

        int stock = goods.getStockCount();
        if(stock <= 0) {
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //判断是否已经秒杀到了
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(),  goodsId);
        System.out.println(order);
        if(order != null) {
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }
        //减库存 下订单 写入秒杀订单
        OrderInfo orderInfo = miaoshaService.miaosha(user, goods);
        return Result.success(orderInfo);
    }
}
