package com.born.secKill02.utils;

import java.util.UUID;

/**
 * @Description:
 * @Since: jdk1.8
 * @Author: gyk
 * @Date: 2020-04-11 09:17:58
 */
public class UUIDUtils {

    public static String uuid(){
        return UUID.randomUUID().toString().replace("-", "");
    }

}
