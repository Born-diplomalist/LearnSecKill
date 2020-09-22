package com.born.secKill02.mapper;

import com.born.secKill02.entity.SecKill;
import com.born.secKill02.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * @Since: jdk1.8
 * @Author: gyk
 * @Date: 2020-04-09 09:58:42
 */
@Mapper
public interface ISecKillMapper {

	//扣减秒杀商品库存
	@Update("update sec_kill set sec_kill_stock = sec_kill_stock - 1 where sec_kill_goods_id = #{goodsVo.goodsId}")
	int reduceStock(@Param("goodsVo") GoodsVo goodsVo);

	//设置库存
	@Update("update sec_kill set sec_kill_stock = #{secKillStock} where sec_kill_goods_id = #{secKillGoodsId}")
	void resetStock(SecKill secKill);

	@Select("select sec_kill_id as secKillId,sec_kill_stock as secKillStock from sec_kill where sec_kill_goods_id=#{goodsId}")
    SecKill getSecKillByGoodsId(long goodsId);
}
