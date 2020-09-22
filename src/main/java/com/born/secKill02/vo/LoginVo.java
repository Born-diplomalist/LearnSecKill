package com.born.secKill02.vo;

import com.born.secKill02.config.validator.IsMobile;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * @Description: 登录参数对象
 * @Since: jdk1.8
 * @Author: gyk
 * @Date: 2020-04-10 12:23:09
 */
@Data
@Getter
@Setter
@ToString
public class LoginVo {

    @NotNull
    @IsMobile
    private String mobile;

    @NotNull
    @Length(min=32)
    private String password;

}
