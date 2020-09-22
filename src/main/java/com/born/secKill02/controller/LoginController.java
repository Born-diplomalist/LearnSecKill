package com.born.secKill02.controller;

import com.born.secKill02.common.JsonBean;
import com.born.secKill02.common.Status;
import com.born.secKill02.exception.GlobalException;
import com.born.secKill02.service.IUserService;
import com.born.secKill02.vo.LoginVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @Description: 登录控制器
 * @Since: jdk1.8
 * @Author: gyk
 * @Date: 2020-04-10 12:24:15
 */
@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private IUserService userService;

    private static Logger log= LoggerFactory.getLogger(LoginController.class);

    @RequestMapping("/to_login")
    public String toLogin(){
        return "login";
    }

    /**
     * 登录
     *
     * 1.参数校验
     * @param loginVo
     * @return
     */
    @RequestMapping("/do_login")
    @ResponseBody
    public JsonBean<String> doLogin(HttpServletResponse response, @Valid LoginVo loginVo){
    	log.info(loginVo.toString());
    	//登录
        String token = userService.login(response, loginVo);
        return new JsonBean<>(Status.SUCCESS,token);
    }
}
