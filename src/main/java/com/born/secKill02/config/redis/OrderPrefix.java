package com.born.secKill02.config.redis;

/**
 * @Description:
 * @Since: jdk1.8
 * @Author: gyk
 * @Date: 2020-04-27 16:51:55
 */
public class OrderPrefix extends BasePrefix{


    /**
     * 默认永不过期
     *
     * @param prefix
     */
    public OrderPrefix(String prefix) {
        super(prefix);
    }

    public OrderPrefix(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static OrderPrefix getSecKillOrderByUserIdGoodsId=new OrderPrefix("soug");
}
