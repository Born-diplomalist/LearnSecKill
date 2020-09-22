package com.born.secKill02.entity;

import lombok.Data;

/**
 * @Description: 秒杀的订单表-存储唯一的秒杀购买信息
 * @Since: jdk1.8
 * @Author: gyk
 * @Date: 2020-04-11 08:43:28
 */

@Data
public class SecKillOrder {
    private Long secKillOrderId;

    private Long secKillUserId;

    private Long secKillOrderInfoId;

    private Long secKillGoodsId;
}
