package com.born.secKill02.common;

import lombok.Getter;
import lombok.ToString;

/**
 * @Description: 前后端统一数据传递格式封装
 * @Since: jdk1.8
 * @Author: gyk
 * @Date: 2020-04-09 09:41:41
 */
@Getter
@ToString
public class JsonBean<T> {

    private int code;

    private String msg;

    private T data;

    public JsonBean(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public JsonBean(Status status) {
        this.code=status.getCode();
        this.msg=status.getMsg();
    }

    public JsonBean(Status status,T data) {
        this.code=status.getCode();
        this.msg=status.getMsg();
        this.data = data;
    }
}
