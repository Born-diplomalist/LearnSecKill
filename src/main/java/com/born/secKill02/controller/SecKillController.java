package com.born.secKill02.controller;

import com.born.secKill02.access.AccessLimit;
import com.born.secKill02.common.JsonBean;
import com.born.secKill02.common.Status;
import com.born.secKill02.config.redis.GoodsPrefix;
import com.born.secKill02.config.redis.OrderPrefix;
import com.born.secKill02.config.redis.SecKillPrefix;
import com.born.secKill02.entity.OrderInfo;
import com.born.secKill02.entity.SecKillOrder;
import com.born.secKill02.entity.User;
import com.born.secKill02.rabbitmq.MQSender;
import com.born.secKill02.rabbitmq.SecKillMessage;
import com.born.secKill02.service.IGoodsService;
import com.born.secKill02.service.IOrderService;
import com.born.secKill02.service.ISecKillService;
import com.born.secKill02.service.RedisService;
import com.born.secKill02.vo.GoodsVo;
import com.born.secKill02.vo.SeckillDetailVo;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 秒杀
 * @Since: jdk1.8
 * @Author: gyk
 * @Date: 2020-04-16 10:17:24
 */
@Controller
@RequestMapping("/secKill")
public class SecKillController{

    //一个service只应该调用自己的mapper和其他的service，不要调用其他的mapper。因为其他的service可能对对应的mapper做了缓存等处理

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private IOrderService orderService;

    @Autowired
    private ISecKillService secKillService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private MQSender mqSender;


    /**
     * 内存标记，标记每个商品的库存状态，如果某商品售空，对应的value就会为true
     * --- 减少对Redis的访问
     */
    private Map<Long,Boolean> localSellOverMap=new HashMap<>();


    /**
     * 初始化Redis缓存中的商品信息
     * 1.查数据库
     * 2.将商品加载到缓存中，如果库存大于0，在内存标记中将其标记为false，否则标记为true
     */
    public void initGoodsInfoToRedis(){
        List<GoodsVo> goodsVoList = goodsService.listGoodsVo();
        if (goodsVoList==null){
            return;
        }
        for (GoodsVo goodsVo : goodsVoList) {
            redisService.set(GoodsPrefix.getSecKillGoodsStock,""+goodsVo.getGoodsId(), goodsVo.getSecKillStock());
            localSellOverMap.put(goodsVo.getGoodsId(), goodsVo.getSecKillStock() <= 0);
        }
    }


    /**
     * 重置数据
     * @param model
     * @return
     */
    @GetMapping("/reset")
    @ResponseBody
    public JsonBean<Boolean> reset(Model model){
        List<GoodsVo> goodsVoList = goodsService.listGoodsVo();
        for (GoodsVo goodsVo:goodsVoList) {
            goodsVo.setSecKillStock(10);
            redisService.set(GoodsPrefix.getSecKillGoodsStock,""+goodsVo.getGoodsId(),10);
            localSellOverMap.put(goodsVo.getGoodsId(),false);
        }
        redisService.delete(OrderPrefix.getSecKillOrderByUserIdGoodsId);
        redisService.delete(SecKillPrefix.isGoodsOver);
        secKillService.reset(goodsVoList);
        return new JsonBean<>(Status.SUCCESS,true);
    }




    /**
     * 执行秒杀，成功后跳转到秒杀详情页
     *
     * 1.判断用户信息，为空返回服务器异常
     * 2.验证path参数，验证通过才能拿到秒杀地址；验证不通过说明请求不合法
     * 3.判断内存标记，如果标记为true，直接返回秒杀结束无需再去查询Redis
     * 4.预减判断库存
     *
     *
     *
     * 1.接收参数 商品ID
     * 2.查商品，判断库存
     * 3.查订单，判断是否已经秒杀过
     * 4.调用秒杀，并将生成的订单详情信息放入Model
     * 5.将订单信息和商品信息写入model
     */
    @PostMapping("/{path}/do_secKill")
    @ResponseBody
    public JsonBean<Integer> doSecKill(Model model, @RequestParam("goodsId") Long goodsId, User user,@PathVariable("path")String path) {
        model.addAttribute("user", user);
        if (user==null){
            return new JsonBean<>(Status.SESSION_ERROR);
        }
        //验证path
        boolean checkResult=secKillService.checkPath(user,goodsId,path);
        if (!checkResult){
            return new JsonBean<>(Status.REQUEST_INLEGAL);
        }
        //内存标记，减少Redis访问
        if (localSellOverMap.get(goodsId)){
            return new JsonBean(Status.SECKILL_SELL_OVER);
        }
        //预减Redis库存并判断剩余库存
        if (redisService.decr(GoodsPrefix.getSecKillGoodsStock,""+goodsId)<0){
            localSellOverMap.put(goodsId,true);
            return new JsonBean(Status.SECKILL_SELL_OVER);
        }


        //判断是否重复秒杀
        SecKillOrder secKillOrder=orderService.getSecKillOrderByUserIdAndGoodsId(user.getUserId(),goodsId);
        if (secKillOrder!=null){
            //model.addAttribute("errorMsg", Status.SECKILL_HAS_OVER);
            return new JsonBean<>(Status.SECKILL_REPEAT);
            //return "secKill_fail";
        }

        //添加到消息队列
        SecKillMessage secKillMessage = new SecKillMessage();
        secKillMessage.setUser(user);
        secKillMessage.setGoodsId(goodsId);
        mqSender.sendMiaoshaMessage(secKillMessage);
        return new JsonBean(Status.SUCCESS,0);

        ////判断数据库库存
        //GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        //if (goodsVo.getSecKillStock()<=0){
        //    //model.addAttribute("errorMsg", Status.SECKILL_HAS_OVER);
        //    return new JsonBean<>(Status.SECKILL_SELL_OVER);
        //    //return "secKill_fail";
        //}



        //OrderInfo orderInfo= secKillService.secondKill(user,goodsVo);
        //SeckillDetailVo seckillDetailVo = new SeckillDetailVo();
        //seckillDetailVo.setGoodsVo(goodsVo);
        //seckillDetailVo.setOrderInfo(orderInfo);
        ////model.addAttribute("orderInfo", orderInfo);
        ////model.addAttribute("goodsVo", goodsVo);
        //return new JsonBean(Status.SUCCESS,seckillDetailVo);
        ////return "order_detail";
    }


    //获取秒杀结果（是否成功）
    @RequestMapping(value="/result", method=RequestMethod.GET)
    @ResponseBody
    public JsonBean<Long> miaoshaResult(Model model,User user,
    		@RequestParam("goodsId")long goodsId) {
    	model.addAttribute("user", user);
    	if(user == null) {
    		return new JsonBean<>(Status.SESSION_ERROR);
    	}
    	long result  =secKillService.getSecKillResult(user.getUserId(), goodsId);
    	return new JsonBean<>(Status.SUCCESS,result);
    }

    /**
     * 获取秒杀地址
     * @param request
     * @param user
     * @param goodsId
     * @param verifyCode
     * @return
     */
    @AccessLimit(seconds=5, maxCount=5, needLogin=true) //访问频率限制  防刷
    @RequestMapping(value="/path", method=RequestMethod.GET)
    @ResponseBody
    public JsonBean<String> getSecKillPath(HttpServletRequest request, User user,
                                         @RequestParam("goodsId")long goodsId,
                                         @RequestParam(value="verifyCode", defaultValue="0")int verifyCode
    		) {
    	if(user == null) {
    		return new JsonBean<>(Status.SESSION_ERROR);
    	}
    	boolean check = secKillService.checkVerifyCode(user, goodsId, verifyCode);
    	if(!check) {
    		return new JsonBean<>(Status.REQUEST_INLEGAL);
    	}
    	String path  =secKillService.createSecKillPath(user, goodsId);
    	return new JsonBean<>(Status.SUCCESS,path);
    }


    @RequestMapping(value="/verifyCode", method=RequestMethod.GET)
    @ResponseBody
    public JsonBean<String> getMiaoshaVerifyCod(HttpServletResponse response, User user,
                                              @RequestParam("goodsId")long goodsId) {
    	if(user == null) {
    		return new JsonBean<>(Status.SESSION_ERROR);
    	}
    	try {
    		BufferedImage image  = secKillService.createVerifyCode(user, goodsId);
    		OutputStream out = response.getOutputStream();
    		ImageIO.write(image, "JPEG", out);
    		out.flush();
    		out.close();
			//通过输出流OutputStream返回，不需要通过返回值
    		return null;
    	}catch(Exception e) {
    		e.printStackTrace();
    		return new JsonBean<>(Status.SECKILL_FAIL);
    	}
    }

}
