package com.linbin.miaosha.rabbitmq;

import com.linbin.miaosha.domain.MiaoshaOrder;
import com.linbin.miaosha.domain.MiaoshaUser;
import com.linbin.miaosha.redis.RediService;
import com.linbin.miaosha.service.GoodsService;
import com.linbin.miaosha.service.MiaoshaService;
import com.linbin.miaosha.service.OrderService;
import com.linbin.miaosha.vo.GoodsVo;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;


@Service
public class MQReveicer {
    private static Logger log = LoggerFactory.getLogger(MQReveicer.class);

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;


    @Autowired
    MiaoshaService miaoshaService;

    @RabbitListener(queues = com.imooc.miaosha.rabbitmq.MQConfig.MIAOSHA_QUEUE)
    public void receive(String message) {
        log.info("receive message:" + message);
        MiaoshaMessage mm = RediService.StringToBean(message, MiaoshaMessage.class);
        MiaoshaUser user = mm.getUser();
        long goodsId = mm.getGoodsId();

        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goods.getStockCount();
        if (stock <= 0) {
            return;
        }
        //判断是否已经秒杀到了
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null) {
            return;
        }
        //减库存 下订单 写入秒杀订单
        miaoshaService.miaosha(user, goods);
    }
}