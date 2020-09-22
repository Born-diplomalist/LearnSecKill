package com.born.secKill02.config.param;

import com.born.secKill02.access.AccessInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

/**
 * @Description:
 * @Since: jdk1.8
 * @Author: gyk
 * @Date: 2020-04-11 08:31:19
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {
    @Autowired
    private UserArgumentResolver userArgumentResolver;

    @Autowired
    AccessInterceptor accessInterceptor;

    /**
     * 所谓的服务器为我们注入的HttpServletRequest之类的参数，都是在这里注入的
     * @param argumentResolvers
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        //添加一个参数处理
        argumentResolvers.add(userArgumentResolver);
    }

    @Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(accessInterceptor);
	}
}
