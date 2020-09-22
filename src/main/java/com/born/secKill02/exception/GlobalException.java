package com.born.secKill02.exception;

import com.born.secKill02.common.Status;

/**
 * @Description: 全局异常处理
 * @Since: jdk1.8
 * @Author: gyk
 * @Date: 2020-04-11 10:35:58
 */
public class GlobalException extends RuntimeException {

    private static final  long serialVersionUID=1L;

    private Status status;

    public GlobalException(Status status){
        super(status.toString());
        this.status=status;
    }

    public Status getStatus() {
        return status;
    }
}
