package com.born.secKill02.config.redis;

/**
 * @Description:
 * @Since: jdk1.8
 * @Author: gyk
 * @Date: 2020-04-11 12:40:33
 */
public class GoodsPrefix extends BasePrefix {

    public static final int TOKEN_EXPIRE=3600*24*2;

    public GoodsPrefix(int expireSeconds, String prefix) {
        super(expireSeconds,prefix);
    }

    //public static GoodsPrefix token=new GoodsPrefix(TOKEN_EXPIRE,"tk");
    public static GoodsPrefix getGoodsList=new GoodsPrefix(60,"gl");
    public static GoodsPrefix getGoodsDetail=new GoodsPrefix(60,"gd");
    public static GoodsPrefix getSecKillGoodsStock=new GoodsPrefix(0,"gs");






}
