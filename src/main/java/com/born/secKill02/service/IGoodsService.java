package com.born.secKill02.service;

import com.born.secKill02.vo.GoodsVo;

import java.util.List;

/**
 * @Since: jdk1.8
 * @Author: gyk
 * @Date: 2020-04-12 09:14:27
 */
public interface IGoodsService {

    List<GoodsVo> listGoodsVo();

    GoodsVo getGoodsVoByGoodsId(Long goodsId);
}
