package com.born.secKill02.exception;

import com.born.secKill02.common.JsonBean;
import com.born.secKill02.common.Status;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Description:
 * @Since: jdk1.8
 * @Author: gyk
 * @Date: 2020-04-11 10:06:14
 */
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    /**
     * 异常统一处理
     * 1.判断为某一类型异常
     * 2.将Exception对象强转为对应类型异常
     * 3.设定好异常消息，返回
     *
     */
    @ExceptionHandler(value = Exception.class)
    public JsonBean<String> exceptionHandler(HttpServletRequest request,Exception e){
        e.printStackTrace();//打印错误信息的日志到控制台
        if (e instanceof GlobalException){
            GlobalException globalException = (GlobalException) e;
            return new JsonBean<>(globalException.getStatus());
        }
        //非绑定异常暂且统一返回服务器异常
        else if (e instanceof BindException){
            //处理绑定异常
            BindException bindException=(BindException)e;
            List<ObjectError> errorList = bindException.getAllErrors();
            ObjectError error = errorList.get(0);
            return new JsonBean<>(Status.BIND_ERROR.fillArgs(error.getDefaultMessage()));
        }else {
            return new JsonBean<>(Status.SERVER_ERROR);
        }
    }
}
