package com.born.secKill02.entity;

import lombok.Data;

/**
 * @Description: 商品表实体类
 * @Since: jdk1.8
 * @Author: gyk
 * @Date: 2020-04-11 08:36:27
 */
@Data
public class Goods {

    private Long goodsId;

    private String goodsName;

    private String goodsTitle;

    private String goodsMsg;

    private String goodsImg;

    private String goodsDetail;

    private Double goodsPrice;

    private Integer goodsStock;

}
