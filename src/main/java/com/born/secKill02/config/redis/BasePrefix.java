package com.born.secKill02.config.redis;

/**
 * 设计模式---模板模式：实现接口的抽象类，实现一些通用设置，其他设置让实现类实现
 * @Description: 前缀的抽象类，设定默认永不过期
 * @Since: jdk1.8
 * @Author: gyk
 * @Date: 2020-04-09 21:36:18
 */
public abstract class BasePrefix implements KeyPrefix{

    private int expireSeconds;

    /**
     * 前缀，用于区分不同模块的同名key
     * 设置时还是原来的key，但是在存和取的时候，都会默认地使用添加了前缀后的key
     */
    private String prefix;

    /**
     * 默认永不过期
     */
    public BasePrefix(String prefix){
        this(0,prefix);
    }

    public BasePrefix(int expireSeconds, String prefix) {
        this.expireSeconds=expireSeconds;
        this.prefix=prefix;
    }

    /**
     * 默认值为0，即默认永不过期
     * @return 过期时间
     */
    @Override
    public int getExpireSeconds(){
        return expireSeconds;
    }

    /**
     * 获取前缀
     * @return 前缀
     */
    @Override
    public String getPrefix() {
        String className=getClass().getSimpleName();
        return className+":"+prefix;
    }
}
