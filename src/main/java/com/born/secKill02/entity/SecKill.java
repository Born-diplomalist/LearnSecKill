package com.born.secKill02.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description: 秒杀商品表
 * @Since: jdk1.8
 * @Author: gyk
 * @Date: 2020-04-11 08:41:13
 */
@Data
public class SecKill {

    private Long secKillId;

    private Long secKillGoodsId;

    private BigDecimal secKillPrice;

    private Integer secKillStock;

    private Date secKillStartDate;

    private Date secKillEndDate;
}
