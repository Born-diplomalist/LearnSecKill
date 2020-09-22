package com.born.secKill02.utils;

import com.alibaba.fastjson.JSON;

/**
 * @Description: Json工具类
 * @Since: jdk1.8
 * @Author: gyk
 * @Date: 2020-04-09 22:19:41
 */
public class JsonUtil {

        /**
     * Bean转换为String
     * <p>
     * 1. 判断是否为null
     * 2. 判断是否是一些基础的类型，相对应处理
     * 3.如果不是，认定为是一个Bean，将这个Bean使用Json的API转换为String
     *
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> String beanToString(T obj) {
        if (obj == null) {
            return null;
        }
        Class<?> clazz = obj.getClass();
        if (clazz == int.class || clazz == Integer.class || clazz == Long.class) {
            return "" + obj;
        } else if (clazz == String.class) {
            return (String) obj;
        } else {
            return JSON.toJSONString(obj);
        }
    }


    /**
     * String转为Bean
     *
     * @param str
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T stringToBean(String str, Class<T> clazz) {
        if (str == null || str.length() <= 0 || clazz == null) {
            return null;
        } else if (clazz == int.class || clazz == Integer.class) {
            return (T) Integer.valueOf(str);
        } else if (clazz == String.class) {
            return (T) str;
        } else if (clazz == Long.class) {
            return (T) Long.valueOf(str);
        }
        return JSON.toJavaObject(JSON.parseObject(str), clazz);
    }


}
