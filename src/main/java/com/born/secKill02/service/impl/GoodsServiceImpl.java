package com.born.secKill02.service.impl;

import com.born.secKill02.mapper.IGoodsMapper;
import com.born.secKill02.service.IGoodsService;
import com.born.secKill02.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 商品
 * @Since: jdk1.8
 * @Author: gyk
 * @Date: 2020-04-12 09:15:01
 */
@Service
public class GoodsServiceImpl implements IGoodsService {

    @Autowired
    private IGoodsMapper goodsMapper;

    /**
     * 获取所有商品信息及其秒杀信息
     * @return
     */
    @Override
    public List<GoodsVo> listGoodsVo() {
        return goodsMapper.listGoodsVos();
    }

    /**
     * 获取某商品信息及其秒杀信息
     * @param goodsId
     * @return
     */
    public GoodsVo getGoodsVoByGoodsId(Long goodsId) {
        return goodsMapper.getGoodsVoByGoodsId(goodsId);
    }

    //减商品表中商品库存
    //public boolean reduceStock(GoodsVo goods) {
	//	MiaoshaGoods g = new MiaoshaGoods();
	//	g.setGoodsId(goods.getId());
	//	int ret = goodsDao.reduceStock(g);
	//	return ret > 0;
	//}

	//重置商品表中商品库存
//	public void resetStock(List<GoodsVo> goodsList) {
//		for(GoodsVo goods : goodsList ) {
//			MiaoshaGoods g = new MiaoshaGoods();
//			g.setGoodsId(goods.getId());
//			g.setStockCount(goods.getStockCount());
//			goodsDao.resetStock(g);
//		}
//	}
}
