package com.linbin.miaosha.redis;

import com.linbin.miaosha.domain.Goods;

public class MiaoshaKey extends BasePrefix{
    public MiaoshaKey(String prefix) {
        super(prefix);
    }

    public static MiaoshaKey isGoodsOver = new MiaoshaKey("go");
}
