package com.born.secKill02.entity;


import lombok.Data;

import java.util.Date;

/**
 * @Description: 用户
 * @Since: jdk1.8
 * @Author: gyk
 * @Date: 2020-04-09 09:59:29
 */
@Data
public class User {
    /**
     * 用户ID  也即手机号
     */
    private Long userId;

    private String userNickName;

    private String userPassword;

    private String userSalt;

    private String userHead;

    private Date userRegisterDate;

    private Date userLastLoginDate;

    private Integer userLoginCount;

}
