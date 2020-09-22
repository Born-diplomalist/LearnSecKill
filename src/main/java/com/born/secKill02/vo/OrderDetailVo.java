package com.born.secKill02.vo;

import com.born.secKill02.entity.OrderInfo;
import lombok.Data;

/**
 * @Description:
 * @Since: jdk1.8
 * @Author: gyk
 * @Date: 2020-08-03 10:00:30
 */
@Data
public class OrderDetailVo {

    private GoodsVo goodsVo;

    private OrderInfo orderInfo;

}
