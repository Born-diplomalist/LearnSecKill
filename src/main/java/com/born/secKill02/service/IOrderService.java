package com.born.secKill02.service;
import java.util.Date;

import com.born.secKill02.config.redis.OrderPrefix;
import com.born.secKill02.entity.OrderInfo;
import com.born.secKill02.entity.SecKill;
import com.born.secKill02.entity.SecKillOrder;
import com.born.secKill02.entity.User;
import com.born.secKill02.mapper.IOrderMapper;
import com.born.secKill02.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description:
 * @Since: jdk1.8
 * @Author: gyk
 * @Date: 2020-04-16 12:11:57
 */
@Service
public class IOrderService {

    @Autowired
    private IOrderMapper orderMapper;

    @Autowired
    private RedisService redisService;

    public SecKillOrder getSecKillOrderByUserIdAndGoodsId(Long userId, Long goodsId) {
        //return orderMapper.getSecKillOrderByUserIdAndGoodsId(userId,goodsId);
        return redisService.get(OrderPrefix.getSecKillOrderByUserIdGoodsId, ""+userId+"_"+goodsId, SecKillOrder.class);
    }


    public SecKillOrder getOrderById(Long orderId){
     return orderMapper.getOrderById(orderId);
    }

    /**
     * 下订单
     *
     * 1.准备数据
     * 2.生成订单详情，并返回生成的订单详情ID
     * 3.使用得到的订单详情ID生成秒杀订单记录
     *
     * @param user 用户信息
     * @param goodsVo 秒杀商品信息
     * @return 生成的订单详情
     */
    @Transactional
    public OrderInfo createOrder(User user, GoodsVo goodsVo) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderInfoUserId(user.getUserId());
        orderInfo.setOrderInfoGoodsId(goodsVo.getGoodsId());
        orderInfo.setOrderInfoDeliveryAddrId(0L);
        orderInfo.setOrderInfoGoodsName(goodsVo.getGoodsName());
        orderInfo.setOrderInfoGoodsCount(goodsVo.getSecKillStock());
        orderInfo.setOrderInfoGoodsPrice(goodsVo.getSecKillPrice());
        orderInfo.setOrderInfoOrderChannel(1);
        orderInfo.setOrderInfoStatus(0);
        orderInfo.setOrderInfoCreateDate(new Date());
        //获取生成的订单详情ID
        long orderInfoId=orderMapper.insertOrderInfo(orderInfo);

        SecKillOrder secKillOrder = new SecKillOrder();
        secKillOrder.setSecKillUserId(user.getUserId());
        secKillOrder.setSecKillOrderInfoId(orderInfoId);
        secKillOrder.setSecKillGoodsId(goodsVo.getGoodsId());
        long secKillOrderId = orderMapper.insertSecKillOrder(secKillOrder);
        //将生成的秒杀订单信息设置完生成的订单ID后存入redis（如果不设置，在redis中secKillOrderId将为null）
        secKillOrder.setSecKillOrderId(secKillOrderId);
        redisService.set(OrderPrefix.getSecKillOrderByUserIdGoodsId, ""+user.getUserId()+"_"+goodsVo.getGoodsId(), secKillOrder);
        return orderInfo;
    }

    public OrderInfo getOrderInfoByOrderInfoId(Long orderId){
        return orderMapper.getOrderInfoByOrderInfoId(orderId);
    }

    //清空订单和订单详情记录
    public void cleanOrder() {
        orderMapper.deleteOrder();
        orderMapper.deleteOrderInfo();
    }
}
