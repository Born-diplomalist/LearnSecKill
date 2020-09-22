package com.born.secKill02.vo;

import com.born.secKill02.entity.User;
import lombok.Data;

import java.util.Date;

/**
 * @Description:
 * @Since: jdk1.8
 * @Author: gyk
 * @Date: 2020-04-27 08:18:05
 */
@Data
public class GoodsDetailVo {

    private int secKillStatus=0;

    private int remainSeconds=0;

    private GoodsVo goodsVo;

    private User user;

}
