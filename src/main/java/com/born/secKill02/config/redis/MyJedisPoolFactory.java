package com.born.secKill02.config.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @Description:
 * @Since: jdk1.8
 * @Author: gyk
 * @Date: 2020-04-09 20:29:01
 */
@Configuration
public class MyJedisPoolFactory {

    @Autowired
    RedisConfig redisConfig;

    @Bean
    public JedisPool jedisPoolFactory(){
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(redisConfig.getPoolMaxIdle());
        jedisPoolConfig.setMaxTotal(redisConfig.getPoolMaxTotal());
        jedisPoolConfig.setMaxWaitMillis(redisConfig.getPoolMaxWait()*1000);
        return new JedisPool(jedisPoolConfig, redisConfig.getHost(),redisConfig.getPort(), redisConfig.getTimeOut() * 1000);
        //return new JedisPool(jedisPoolConfig, redisConfig.getHost(),redisConfig.getPort(), redisConfig.getTimeOut() * 1000, redisConfig.getPassword(), 0);//0表示使用redis的0号库，这也是默认行为
    }

}
