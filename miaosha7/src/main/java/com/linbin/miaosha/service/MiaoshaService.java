package com.linbin.miaosha.service;

import com.linbin.miaosha.Util.MD5Util;
import com.linbin.miaosha.Util.UUIDUtil;
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

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

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
    public boolean checkPath(MiaoshaUser user, long goodsId, String path) {
        if(user == null || path == null) {
            return false;
        }
        String pathOld = rediService.get(MiaoshaKey.getMiaoshaPath, ""+user.getId() + "_"+ goodsId, String.class);
        return path.equals(pathOld);
    }

    public String createMiaoshaPath(MiaoshaUser user, long goodsId) {
        if(user == null || goodsId <=0) {
            return null;
        }
        String str = MD5Util.md5(UUIDUtil.uuid()+"123456");
        rediService.set(MiaoshaKey.getMiaoshaPath, ""+user.getId() + "_"+ goodsId, str);
        return str;
    }

    public BufferedImage createVerifyCode(MiaoshaUser user, long goodsId) {
        if(user == null || goodsId <=0) {
            return null;
        }
        int width = 80;
        int height = 32;
        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));//背景颜色
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.black);//黑色图形框
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // generate a random code
        String verifyCode = generateVerifyCode(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();
        //把验证码存到redis中
        int rnd = calc(verifyCode);
        rediService.set(MiaoshaKey.getMiaoshaVerifyCode, user.getId()+","+goodsId, rnd);
        //输出图片
        return image;
    }
    private static char[] ops = new char[] {'+', '-', '*'};
    /**
     * + - *
     * */
    private String generateVerifyCode(Random rdm) {
        int num1 = rdm.nextInt(10);
        int num2 = rdm.nextInt(10);
        int num3 = rdm.nextInt(10);
        char op1 = ops[rdm.nextInt(3)];
        char op2 = ops[rdm.nextInt(3)];
        String exp = ""+ num1 + op1 + num2 + op2 + num3;
        return exp;
    }
    private static int calc(String exp) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            return (Integer)engine.eval(exp);
        }catch(Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public boolean checkVerifyCode(MiaoshaUser user, long goodsId, int verifyCode)
    {
        if(user == null || goodsId <=0) {
            return false;
        }
        Integer codeOld = rediService.get(MiaoshaKey.getMiaoshaVerifyCode, user.getId()+","+goodsId, Integer.class);
        if(codeOld == null || codeOld - verifyCode != 0 ) {
            return false;
        }
        rediService.delete(MiaoshaKey.getMiaoshaVerifyCode, user.getId()+","+goodsId);
        return true;
    }
}
