package com.born.secKill02.config.param;

import com.born.secKill02.access.UserContext;
import com.born.secKill02.entity.User;
import com.born.secKill02.service.IUserService;
import com.born.secKill02.service.impl.UserServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description: 用户参数处理器
 * @Since: jdk1.8
 * @Author: gyk
 * @Date: 2020-04-11 08:17:01
 */
@Service
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private IUserService userService;

    /**
     * 确定当前参数是否是本类要检验的类型
     * @param parameter 要进行校验的参数
     * @return {@code true} 支持该类型参数的校验
     * {@code false} 不支持该类型参数的校验
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> clazz = parameter.getParameterType();
        return clazz== User.class;
    }

    /**
     * 预处理方法的参数
     *
     * 该方法包含了视图、请求以及参数绑定的参数
     * 可在此处统一处理类似HttpServletRequest、HttpServletResponse这类的参数，并进行验证，这样真正的方法中不需要传递这些参数，直接接收处理后的数据即可
     *
     *  @param parameter     需要解析的参数，该参数需要是supportsParameter所承认的类型
     */
    //@Override
    //public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
    //    HttpServletRequest nativeRequest = webRequest.getNativeRequest(HttpServletRequest.class);
    //    HttpServletResponse nativeResponse = webRequest.getNativeResponse(HttpServletResponse.class);
    //    //从参数request中获取token的值
    //    String paramToken = nativeRequest.getParameter(UserServiceImpl.COOKIE_NAME_TOKEN);
    //    //从cookie中获取token的值
    //    String cookieToken = getCookieValue(nativeRequest, UserServiceImpl.COOKIE_NAME_TOKEN);
    //    //两者都为空，则信息不存在
    //    if(StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
	//		return null;
	//	}
    //    //至少存在一个，则优先从参数中获取token，参数中获取不到再去cookie中获取
    //    String token = StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
    //    //通过token取出数据，将数据返回
    //    return userService.getUserByToken(nativeResponse,token);
    //}

    /**
     * 获取Cookie中指定参数名的参数值
     */
    //private String getCookieValue(HttpServletRequest request,String cookieName){
    //    Cookie[] cookiesArray = request.getCookies();
    //    if(cookiesArray!=null &&cookiesArray.length>0){
    //        for (Cookie cookie:cookiesArray){
    //            if (cookie.getName().equals(cookieName)){
    //                return cookie.getValue();
    //            }
    //        }
    //    }
    //    return null;
    //}

    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		return UserContext.getUser();
	}
}
