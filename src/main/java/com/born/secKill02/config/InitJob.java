package com.born.secKill02.config;

import com.born.secKill02.controller.SecKillController;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Since: jdk1.8
 * @Author: gyk
 * @Date: 2020-08-10 12:18:03
 */
@Service
public class InitJob implements InitializingBean {

    @Autowired
    private SecKillController secKillController;


     /**
     * 初始化操作
     *
     * 在所有的Bean的属性都被设置后，由BeanFactory调用
     * 在所有Bean属性被设置或抛出了异常后，才允许初始化Bean
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet(){
        secKillController.initGoodsInfoToRedis();
        org.apache.ibatis.logging.LogFactory.useSlf4jLogging();
    }

    //
    //@PostConstruct
    //public void initLog(){
    //
    //}
}
