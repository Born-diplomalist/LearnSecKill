package com.born.secKill02.vo;

import com.born.secKill02.entity.OrderInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description:
 * @Since: jdk1.8
 * @Author: gyk
 * @Date: 2020-08-03 09:43:19
 */
@Data
@EqualsAndHashCode
public class SeckillDetailVo {

    private OrderInfo orderInfo;

    private GoodsVo goodsVo;

}
