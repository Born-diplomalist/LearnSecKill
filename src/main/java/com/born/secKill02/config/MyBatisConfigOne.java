package com.born.secKill02.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.apache.ibatis.logging.log4j.Log4jImpl;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

/**
 * 不同于 JdbcTemplate，MyBatis 的配置要稍微麻烦一些，因为要提供两个 Bean，因此这里在两个类中分开来配置
 * <p>
 * <p>
 * 创建 MyBatisConfigOne 类，首先指明该类是一个配置类，配置类中要扫描的包是com.born.mybatis.mapper1 ，即该包下的 Mapper 接口将操作 dsOne 中的数据，
 * 对应的SqlSessionFactory 和 SqlSessionTemplate 分别是 sqlSessionFactory1 和 sqlSessionTemplate1，在MyBatisConfigOne 内部，分别提供 SqlSessionFactory 和 SqlSessionTemplate 即可，
 * SqlSessionFactory 根据 dsOne 创建，然后再根据创建好的SqlSessionFactory 创建一个SqlSessionTemplate。
 *
 * @Description:
 * @Since: jdk1.8
 * @Author: gyk
 * @Date: 2020-07-20 15:39:24
 */

@Configuration
public class MyBatisConfigOne {


    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    DataSource dsOne() {
        return DruidDataSourceBuilder.create().build();
    }


    @Bean
    SqlSessionFactory sqlSessionFactory() {
        SqlSessionFactory sessionFactory = null;
        try {
            SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
            bean.setDataSource(dsOne());
            ////下面三行代码就代替了xml中配置log4j日志，底层xml解析最终也会拼装为下面这个对象
            //org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
            //configuration.setLogImpl(Log4jImpl.class);
            //bean.setConfiguration(configuration);
            sessionFactory = bean.getObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sessionFactory;
    }

    @Bean
    SqlSessionTemplate sqlSessionTemplate() {
        return new SqlSessionTemplate(sqlSessionFactory());
    }
}