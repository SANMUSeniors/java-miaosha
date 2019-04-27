package com.linbin.miaosha.service;

import com.linbin.miaosha.dao.OrderDao;
import com.linbin.miaosha.domain.MiaoshaOrder;
import com.linbin.miaosha.domain.MiaoshaUser;
import com.linbin.miaosha.domain.OrderInfo;
import com.linbin.miaosha.redis.MiaoshaKey;
import com.linbin.miaosha.redis.RediService;
import com.linbin.miaosha.vo.GoodsVo;
import com.linbin.miaosha.vo.miaoshaGoodsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MiaoshaService {
    @Autowired
    GoodsService goodsService;
    @Autowired
    OrderService orderService;
    @Autowired
    RediService rediService;

    public OrderInfo miaosha(MiaoshaUser user, GoodsVo goods) {
        //减库存 下订单 写入秒杀订单
        boolean success =goodsService.reduceStock(goods);
        if(success)
        {
            return orderService.createOrder(user,goods);
        }else
        {
            setGoodsOver(goods.getId());
            return null;
        }
    }

    public long getMiaoshaResult(Long usderId, long goodsId) {
        MiaoshaOrder order =orderService.getMiaoshaOrderByUserIdGoodsId(usderId,goodsId);
        if(order!=null)
        {
            return order.getOrderId();
        }else
        {
            boolean isOver =getGoodsOver(goodsId);
            if (isOver)
            {
                return -1;
            }else
            {
                return 0;
            }
        }
    }

    public void setGoodsOver(Long goodsid) {

       rediService.set(MiaoshaKey.isGoodsOver,""+goodsid,true);
    }

    public boolean getGoodsOver(Long goodsid) {
        return rediService.exists(MiaoshaKey.isGoodsOver,""+goodsid);
    }

    public void reset(List<GoodsVo> goodsList) {
        goodsService.resetStock(goodsList);
        orderService.deleteOrders();
    }
}
