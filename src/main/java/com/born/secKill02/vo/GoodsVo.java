package com.born.secKill02.vo;

import com.born.secKill02.entity.Goods;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Date;

/**
 * @Description:
 * @Since: jdk1.8
 * @Author: gyk
 * @Date: 2020-04-11 09:36:04
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GoodsVo extends Goods {

    private Double secKillPrice;

    private Integer secKillStock;

    private Date secKillStartDate;

    private Date secKillEndDate;
}
