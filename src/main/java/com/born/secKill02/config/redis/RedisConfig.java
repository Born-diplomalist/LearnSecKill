package com.born.secKill02.config.redis;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @Description: Redis客户端自定义配置
 * @Since: jdk1.8
 * @Author: gyk
 * @Date: 2020-04-09 17:02:32
 */
@Component
@ConfigurationProperties(prefix = "redis")
@Data
public class RedisConfig {
    //private String host="192.168.235.131";
    private String host="127.0.0.1";
    private Integer port=6379;
    private Integer timeOut=3;//秒
    //private String password="123456";
    private Integer poolMaxTotal=10;
    private Integer poolMaxIdle=10;
    private Integer poolMaxWait=3;//秒
}
