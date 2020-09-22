package com.born.secKill02.service;

import com.born.secKill02.common.JsonBean;
import com.born.secKill02.entity.User;
import com.born.secKill02.vo.LoginVo;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

/**
 * @Since: jdk1.8
 * @Author: gyk
 * @Date: 2020-04-11 10:42:11
 */
public interface IUserService {

    User getUserById(Long userId);

    //登录
    String login(HttpServletResponse response, LoginVo loginVo);

    //通过token从分布式Session拿到用户数据
    User getUserByToken(HttpServletResponse response,String token);
}
