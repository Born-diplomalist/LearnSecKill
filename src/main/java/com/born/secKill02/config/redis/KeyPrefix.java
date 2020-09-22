package com.born.secKill02.config.redis;

/**
 * @Description: key的前缀接口
 * @Since: jdk1.8
 * @Author: gyk
 * @Date: 2020-04-09 21:35:58
 */
public interface KeyPrefix {

    /**
     * 超时时间
     * @return
     */
    int getExpireSeconds();

    /**
     * 前缀
     * @return
     */
    String getPrefix();

}
