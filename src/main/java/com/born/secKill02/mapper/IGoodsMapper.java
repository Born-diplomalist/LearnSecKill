package com.born.secKill02.mapper;

import com.born.secKill02.entity.SecKill;
import com.born.secKill02.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @Since: jdk1.8
 * @Author: gyk
 * @Date: 2020-04-11 09:50:55
 */
@Mapper
public interface IGoodsMapper {

    @Select("select g.goods_id as goodsId,g.goods_name as goodsName,g.goods_title as goodsTitle,g.goods_img as goodsImg,g.goods_detail as goodsDetail,g.goods_price as goodsPrice,g.goods_stock as goodsStock,s.sec_kill_price as secKillPrice,s.sec_kill_stock as secKillStock,s.sec_kill_start_date as secKillStartDate,s.sec_kill_end_date as secKillEndDate from goods g LEFT JOIN sec_kill s ON g.goods_id=s.sec_kill_goods_id")
    //@Select("select g.*,s.sec_kill_price,s.sec_kill_stock,s.sec_kill_start_date,s.sec_kill_end_date from goods g LEFT JOIN sec_kill s ON g.goods_id=s.sec_kill_goods_id")
    List<GoodsVo> listGoodsVos();

    @Select("select g.goods_id as goodsId,g.goods_name as goodsName,g.goods_title as goodsTitle,g.goods_img as goodsImg,g.goods_detail as goodsDetail,g.goods_price as goodsPrice,g.goods_stock as goodsStock,s.sec_kill_price as secKillPrice,s.sec_kill_stock as secKillStock,s.sec_kill_start_date as secKillStartDate,s.sec_kill_end_date as secKillEndDate from goods g LEFT JOIN sec_kill s ON g.goods_id=s.sec_kill_goods_id where g.goods_id=#{goodsId}")
    //@Select("select g.*,s.sec_kill_price,s.sec_kill_stock,s.sec_kill_start_date,s.sec_kill_end_date from goods g LEFT JOIN sec_kill s ON g.goods_id=s.sec_kill_goods_id where g.goods_id=#{goodsId}")
    GoodsVo getGoodsVoByGoodsId(@Param("goodsId") Long goodsId);


}
