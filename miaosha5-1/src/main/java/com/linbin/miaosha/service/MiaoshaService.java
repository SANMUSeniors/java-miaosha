package com.linbin.miaosha.service;

import com.linbin.miaosha.dao.OrderDao;
import com.linbin.miaosha.domain.MiaoshaUser;
import com.linbin.miaosha.domain.OrderInfo;
import com.linbin.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MiaoshaService {
    @Autowired
    GoodsService goodsService;
    @Autowired
    OrderService orderService;

    public OrderInfo miaosha(MiaoshaUser user, GoodsVo goods) {
        //减库存 下订单 写入秒杀订单
        goodsService.reduceStock(goods);
        return orderService.createOrder(user,goods);

    }
}
