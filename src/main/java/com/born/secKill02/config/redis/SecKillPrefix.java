package com.born.secKill02.config.redis;

/**
 * @Description: 秒杀前缀
 * @Since: jdk1.8
 * @Author: gyk
 * @Date: 2020-04-27 09:03:56
 */
public class SecKillPrefix extends BasePrefix {

    public SecKillPrefix(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    /**
     * 商品是否卖完了
     */
    public static SecKillPrefix isGoodsOver=new SecKillPrefix(0,"go");
    /**
     * 秒杀地址获取
     */
    public static SecKillPrefix getSecKillPath=new SecKillPrefix(60,"sp");

    /**
     * 秒杀验证码获取
     */
    public static SecKillPrefix getSecKillVerifyCode=new SecKillPrefix(300,"vc");

}
