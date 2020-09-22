package com.born.secKill02.entity;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * @Description: 订单详情
 * @Since: jdk1.8
 * @Author: gyk
 * @Date: 2020-04-11 08:52:55
 */
@Data
@ToString
public class OrderInfo {

    private Long orderInfoId;

    private Long orderInfoUserId;

    private Long orderInfoGoodsId;

    private Long orderInfoDeliveryAddrId;

    private String orderInfoGoodsName;

    private Integer orderInfoGoodsCount;

    private Double orderInfoGoodsPrice;

    private Integer orderInfoOrderChannel;

    private Integer orderInfoStatus;

    private Date orderInfoCreateDate;

    private Date orderInfoPayDate;


}
