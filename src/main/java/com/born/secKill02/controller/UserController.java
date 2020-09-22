package com.born.secKill02.controller;

import com.born.secKill02.common.JsonBean;
import com.born.secKill02.common.Status;
import com.born.secKill02.entity.User;
import com.born.secKill02.service.IUserService;
import com.born.secKill02.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
    IUserService userService;
	
	@Autowired
    RedisService redisService;
	
    @RequestMapping("/info")
    @ResponseBody
    public JsonBean<User> info(Model model, User user) {
        return new JsonBean(Status.SUCCESS,user);
    }
    
}
