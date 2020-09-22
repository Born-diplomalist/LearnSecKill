package com.born.secKill02.access;

import java.io.OutputStream;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.born.secKill02.common.JsonBean;
import com.born.secKill02.common.Status;
import com.born.secKill02.config.redis.AccessKey;
import com.born.secKill02.entity.User;
import com.born.secKill02.service.ISecKillService;
import com.born.secKill02.service.IUserService;
import com.born.secKill02.service.RedisService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSON;

@Service
public class AccessInterceptor  extends HandlerInterceptorAdapter{
	
	@Autowired
	IUserService userService;
	
	@Autowired
	RedisService redisService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if(handler instanceof HandlerMethod) {
			//从分布式Session中取出用户
			User user = getUser(request, response);
			//保存用户到ThreadLocal，保存到当前线程中
			UserContext.setUser(user);
			HandlerMethod hm = (HandlerMethod)handler;
			AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
			if(accessLimit == null) {
				return true;
			}
			int seconds = accessLimit.seconds();
			int maxCount = accessLimit.maxCount();
			boolean needLogin = accessLimit.needLogin();
			String key = request.getRequestURI();
			if(needLogin) {
				if(user == null) {
					render(response, Status.SESSION_ERROR);
					return false;
				}
				key += "_" + user.getUserId();
			}else {
				//do nothing
			}
			AccessKey ak = AccessKey.withExpire(seconds);
			Integer count = redisService.get(ak, key, Integer.class);
	    	if(count  == null) {
	    		 redisService.set(ak, key, 1);
	    	}else if(count < maxCount) {
	    		 redisService.incr(ak, key);
	    	}else {
	    		render(response, Status.ACCESS_LIMIT_REACHED);
	    		return false;
	    	}
		}
		return true;
	}
	
	private void render(HttpServletResponse response, Status cm)throws Exception {
		response.setContentType("application/json;charset=UTF-8");
		OutputStream out = response.getOutputStream();
		String str  = JSON.toJSONString(new JsonBean(cm));
		out.write(str.getBytes("UTF-8"));
		out.flush();
		out.close();
	}

	private User getUser(HttpServletRequest request, HttpServletResponse response) {
		String paramToken = request.getParameter(ISecKillService.COOKI_NAME_TOKEN);
		String cookieToken = getCookieValue(request, ISecKillService.COOKI_NAME_TOKEN);
		if(StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
			return null;
		}
		String token = StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
		return userService.getUserByToken(response, token);
	}
	
	private String getCookieValue(HttpServletRequest request, String cookiName) {
		Cookie[]  cookies = request.getCookies();
		if(cookies == null || cookies.length <= 0){
			return null;
		}
		for(Cookie cookie : cookies) {
			if(cookie.getName().equals(cookiName)) {
				return cookie.getValue();
			}
		}
		return null;
	}
	
}
