package com.born.secKill02.config.param;

import com.born.secKill02.entity.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * @Description: 从yaml中取出数据库参数
 * @Since: jdk1.8
 * @Author: gyk
 * @Date: 2020-04-24 12:56:33
 */
@Configuration

public class DBConfig {

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    //@Value("s")
    //private DataSource dataSource;

    @Bean
    public Connection DBConn() throws Exception{
        Class.forName(driverClassName);
        return DriverManager.getConnection(url, username, password);
    }

}
