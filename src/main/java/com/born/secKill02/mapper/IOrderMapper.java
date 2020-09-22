package com.born.secKill02.mapper;

import com.born.secKill02.entity.OrderInfo;
import com.born.secKill02.entity.SecKill;
import com.born.secKill02.entity.SecKillOrder;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Since: jdk1.8
 * @Author: gyk
 * @Date: 2020-04-16 12:43:09
 */
@Mapper
public interface IOrderMapper {

    @Select("select * from sec_kill_order where sec_kill_order_user_id=#{userId} and sec_kill_order_goods_id=#{goodsId}")
    SecKillOrder getSecKillOrderByUserIdAndGoodsId(@Param("userId")Long userId, @Param("goodsId")Long goodsId);

    @Insert("insert into order_info (order_info_id,order_info_user_id, order_info_goods_id, order_info_goods_name, order_info_goods_count, order_info_goods_price, order_info_order_channel, order_info_status, order_info_create_date) VALUES(#{orderInfo.orderInfoId},#{orderInfo.orderInfoUserId},#{orderInfo.orderInfoGoodsId},#{orderInfo.orderInfoGoodsName},#{orderInfo.orderInfoGoodsCount},#{orderInfo.orderInfoGoodsPrice},#{orderInfo.orderInfoOrderChannel},#{orderInfo.orderInfoStatus},#{orderInfo.orderInfoCreateDate})")
    //@Insert("insert into order_info (order_info_user_id as orderInfoUserId, order_info_goods_id as orderInfoGoodsId, order_info_goods_name as orderInfoGoodsName, order_info_goods_count as orderInfoGoodsCount, order_info_goods_price as orderInfoGoodsPrice, order_info_order_channel as orderInfoOrderChannel, order_info_status as orderInfoStatus, order_info_create_date as orderInfoCreateDate) VALUES(#{orderInfo.orderInfoUserId},#{orderInfo.orderInfoGoodsId},#{orderInfo.orderInfoGoodsName},#{orderInfo.orderInfoGoodsCount},#{orderInfo.orderInfoGoodsPrice},#{orderInfo.orderInfoOrderChannel},#{orderInfo.orderInfoStatus},#{orderInfo.orderInfoCreateDate})")
    @SelectKey(keyColumn = "order_info_id",keyProperty = "orderInfoId",resultType = long.class,before = false,statement = "select last_insert_id()")
    //@SelectKey(keyColumn = "order_info_id",keyProperty = "orderInfo.orderInfoId",resultType = long.class,before = false,statement = "select last_insert_id()")
    long insertOrderInfo(@Param("orderInfo") OrderInfo orderInfo);

	@Delete("delete from order_info")
    void deleteOrderInfo();

	//根据订单详情ID查询对应订单详情信息
    @Select("select order_info_id as orderInfoId,order_info_user_id as orderInfoUserId,order_info_goods_id as orderInfoGoodsId,order_info_delivery_addr_id as orderInfoDeliveryAddrId,order_info_goods_name as orderInfoGoodsName,order_info_goods_count as orderInfoGoodsCount,order_info_goods_price as orderInfoGoodsPrice,order_info_order_channel as orderInfoOrderChannel,order_info_status as orderInfoStatus, order_info_create_date as orderInfoCreateDate,order_info_pay_date as orderInfoPayDate from order_info where order_info_id=#{orderInfoId}")
    OrderInfo getOrderInfoByOrderInfoId(@Param("orderInfoId")Long orderInfoId);



    @Insert("INSERT INTO sec_kill_order (sec_kill_order_user_id,sec_kill_order_order_info_id,sec_kill_order_goods_id)VALUES(#{secKillOrder.secKillUserId},#{secKillOrder.secKillOrderInfoId},#{secKillOrder.secKillGoodsId}) ")
    @SelectKey(keyColumn = "sec_kill_order_id",keyProperty = "secKillOrderId",resultType = long.class,before = false,statement = "select last_insert_id()")
    long insertSecKillOrder(@Param("secKillOrder") SecKillOrder secKillOrder);

    //根据订单ID查询订单信息
	@Select("select sec_kill_order_id as secKillOrderId,sec_kill_order_user_id as secKillUserId,sec_kill_order_order_info_id as secKillOrderInfoId, sec_kill_order_goods_id as secKillGoodsId from sec_kill_order where  sec_kill_order_id = #{orderId}")
	SecKillOrder getOrderById(@Param("orderId")long orderId);


	@Delete("delete from sec_kill_order ")
    void deleteOrder();

}
