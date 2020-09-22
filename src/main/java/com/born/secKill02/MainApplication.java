package com.born.secKill02;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan(basePackages = "com.born.secKill02.mapper")
@ComponentScan(basePackages = "com.born")
public class MainApplication{


    public static void main(String[] args){
        SpringApplication.run(MainApplication.class, args);
    }


}
