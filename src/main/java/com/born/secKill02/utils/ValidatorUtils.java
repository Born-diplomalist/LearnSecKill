package com.born.secKill02.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * @Description: 自定义验证规则
 * @Since: jdk1.8
 * @Author: gyk
 * @Date: 2020-04-11 09:19:20
 */
public class ValidatorUtils {

    private static final Pattern mobile_pattern=Pattern.compile("1\\d{10}");


    /**
     * 验证指定字符串格式是否符合手机号格式
     */
    public static boolean isMobile(String str){
       if (StringUtils.isEmpty(str)){
           return false;
       }
       return mobile_pattern.matcher(str).matches();
    }

}
