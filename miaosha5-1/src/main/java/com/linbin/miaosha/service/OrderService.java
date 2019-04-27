package com.linbin.miaosha.service;


import com.linbin.miaosha.dao.OrderDao;
import com.linbin.miaosha.domain.MiaoshaOrder;
import com.linbin.miaosha.domain.MiaoshaUser;
import com.linbin.miaosha.domain.OrderInfo;
import com.linbin.miaosha.redis.OrderKey;
import com.linbin.miaosha.redis.RediService;
import com.linbin.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class OrderService {
    @Autowired
    OrderDao orderDao;
    @Autowired
    RediService rediService;

    public MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(Long userId, long goodsId) {
//        return orderDao.getMiaoshaOrderByUserIdGoodsId(id,goodsId);
        return rediService.get(OrderKey.getMiaoshaOrderByUidGid,""+userId+""+goodsId,MiaoshaOrder.class);

    }
    public OrderInfo getOrderById(long orderId) {
        return orderDao.getOrderById(orderId);
    }
    @Transactional
    public OrderInfo createOrder(MiaoshaUser user, GoodsVo goods) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsPrice(goods.getGoodsPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(user.getId());
        long orderId = orderDao.insert(orderInfo);
        MiaoshaOrder  miaoshaOrder = new MiaoshaOrder();
        miaoshaOrder.setGoodsId(goods.getId());
        miaoshaOrder.setOrderId(orderId);
        miaoshaOrder.setUserId(user.getId());
        orderDao.insertMiaoshaOrder(miaoshaOrder);
        rediService.set(OrderKey.getMiaoshaOrderByUidGid,""+user.getId()+""+goods.getId(),miaoshaOrder);
        return orderInfo;
    }
}
