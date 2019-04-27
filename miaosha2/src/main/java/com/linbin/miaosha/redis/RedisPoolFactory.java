package com.linbin.miaosha.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
@Service
public class RedisPoolFactory {
    @Autowired
    Redisconfig redisconfig;
    @Bean
    public JedisPool JedisPoolFactory()
    {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(redisconfig.getPoolMaxIdle());
        poolConfig.setMaxTotal(redisconfig.getPoolMaxTotal());
        poolConfig.setMaxWaitMillis(redisconfig.getPoolMaxWait()*1000);
        JedisPool jp = new JedisPool(poolConfig,redisconfig.getHost(),redisconfig.getPort(),redisconfig.getTimeout()*1000,redisconfig.getPassword(),0);
        //代码是毫秒的,redis是支持多个库的，默认0；
        return jp;
    }
}
