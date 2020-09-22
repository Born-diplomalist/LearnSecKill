package com.born.secKill02.entity;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Since: jdk1.8
 * @Author: gyk
 * @Date: 2020-04-24 13:15:44
 */
@Component
@ConfigurationProperties(prefix = "spring")
@Data
public class DataSource {

    @Value("spring.datasource.username")
    private String username;

    @Value("spring.datasource.password")
    private String password;

    @Value("spring.datasource.password")
    private String url;

    @Value("spring.datasource.driver-class-name")
    private String driverClassName;
    public static void main(String[] args){
        System.out.println(new DataSource().toString());
    }
}
