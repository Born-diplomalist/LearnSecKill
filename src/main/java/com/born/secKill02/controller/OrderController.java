package com.born.secKill02.controller;

import com.born.secKill02.common.JsonBean;
import com.born.secKill02.common.Status;
import com.born.secKill02.entity.OrderInfo;
import com.born.secKill02.entity.SecKillOrder;
import com.born.secKill02.entity.User;
import com.born.secKill02.service.IGoodsService;
import com.born.secKill02.service.IOrderService;
import com.born.secKill02.service.IUserService;
import com.born.secKill02.service.RedisService;
import com.born.secKill02.vo.GoodsVo;
import com.born.secKill02.vo.OrderDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Description:
 * @Since: jdk1.8
 * @Author: gyk
 * @Date: 2020-08-03 09:51:06
 */
@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    RedisService redisService;
    @Autowired
    IOrderService orderService;
    @Autowired
    IGoodsService goodsService;
    @Autowired
    private IUserService userService;

    /**
     * 获取订单详情信息
     *
     * 首先获取用户信息
     * 根据订单ID获取订单详情
     * 根据订单详情得到订单详情中的商品ID
     * 根据商品ID的到商品信息
     * 将商品信息和订单详情信息封装返回
     *
     * @param model
     * @param user
     * @param orderId
     * @return
     */
    @RequestMapping("/detail")
    @ResponseBody
    public JsonBean detail(Model model, User user, @RequestParam("orderId") long orderId) {
        //从分布式session中获取token信息失败
        if (user == null) {
            return new JsonBean(Status.SESSION_ERROR);
        }
        SecKillOrder secKillOrder = orderService.getOrderById(orderId);
        //验证订单是否存在
        if (secKillOrder == null) {
            return new JsonBean(Status.ORDER_NOT_EXIST);
        }
        OrderInfo orderInfo=orderService.getOrderInfoByOrderInfoId(secKillOrder.getSecKillOrderInfoId());
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(orderInfo.getOrderInfoGoodsId());
        OrderDetailVo orderDetailVo = new OrderDetailVo();
        orderDetailVo.setOrderInfo(orderInfo);
        orderDetailVo.setGoodsVo(goodsVo);
        return new JsonBean(Status.SUCCESS, orderDetailVo);


    }


}
