package com.born.secKill02.utils;


import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.HashMap;
import java.util.Map;

/**
 *
 *
 *
 * @Description:
 * @Since: jdk1.8
 * @Author: gyk
 * @Date: 2020-04-11 08:56:41
 */
public class MD5Util {

    //前端加密使用的盐值，为了便于存储和计算，前端需要和此处保持一致。
    private static final String salt="1a2b3d4c";

    //加密
    public static String md5(String str){
        return DigestUtils.md5Hex(str);
    }

    /**
     * 用户输入--> 表单输出结果
     * 原值按一定规则掺杂固定盐值，对结果进行Md5加密
     * 1.自定义规则，将盐和用户输入糅合
     * 2.加密
     */
    public static String inputPassToFormPass(String inputPass){
        //此处规则应和前端加密规则保持一致
        //"" + salt.charAt(0) + salt.charAt(2) + inputPass + salt.charAt(5) + salt.charAt(4)
        String str = ""+salt.charAt(0)+salt.charAt(2) + inputPass +salt.charAt(5) + salt.charAt(4);
		System.out.println(str);
		return md5(str);
    }

    /**
     * 表单输出结果--> 后端DB真正要存储的结果
     *  原值按一定规则掺杂随机的盐值，对结果进行MD5加密，结果和本次盐值都会存入数据库
     */
    public static String formPassToDBPass(String formPass, String salt) {
		String str = ""+salt.charAt(0)+salt.charAt(2) + formPass +salt.charAt(5) + salt.charAt(4);
		return md5(str);
	}

    /**
     * 用户输入---> 存入DB后的结果
     */
    public static String inputPassToDbPass(String inputPass, String saltDB) {
		String formPass = inputPassToFormPass(inputPass);
		String dbPass = formPassToDBPass(formPass, saltDB);
		return dbPass;
	}

    public static void main(String[] args){
        //System.out.println(inputPassToDbPass("123456", "5p99aq"));
        Map<Long, Boolean> hashMap = new HashMap<>();
        Boolean aBoolean = hashMap.get(1L);
        System.out.println(aBoolean);
    }

}
