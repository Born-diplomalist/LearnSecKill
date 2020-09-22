package com.born.secKill02.controller;

import com.born.secKill02.common.JsonBean;
import com.born.secKill02.common.Status;
import com.born.secKill02.config.redis.GoodsPrefix;
import com.born.secKill02.entity.User;
import com.born.secKill02.exception.GlobalException;
import com.born.secKill02.service.IGoodsService;
import com.born.secKill02.service.RedisService;
import com.born.secKill02.vo.GoodsDetailVo;
import com.born.secKill02.vo.GoodsVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.spring4.context.SpringWebContext;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @Description: 商品控制器
 * @Since: jdk1.8
 * @Author: gyk
 * @Date: 2020-04-10 12:24:15
 */
@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private RedisService redisService;

    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;

    @Autowired
    ApplicationContext applicationContext;

    private static Logger log= LoggerFactory.getLogger(GoodsController.class);

    /**
     * 跳转到商品列表页
     * 1.向model中添加用户信息和商品信息
     * 2.使用Thymeleaf视图解析器渲染视图，得到最终html
     * 3.如果html不是空，则将该页面缓存到Redis，定时失效（页面缓存）
     * 4.返回html
     * produces:返回值所映射的媒体类型
     * @param model
     * @param user
     * @return
     */
    @RequestMapping(value = "/to_goodsList")
    //@RequestMapping(value = "/to_goodsList",produces = "text/html")
    @ResponseBody
    public String toList(HttpServletRequest request, HttpServletResponse response, Model model,User user){
        model.addAttribute("user", user);
        model.addAttribute("goodsVoList", goodsService.listGoodsVo());
        SpringWebContext ctx = new SpringWebContext(request,response,
            			request.getServletContext(),request.getLocale(), model.asMap(), applicationContext );
        //手动渲染页面
        String html = thymeleafViewResolver.getTemplateEngine().process("goods_list", ctx);
        if (!StringUtils.isEmpty(html)){
            //渲染成功，则将该页面缓存到Redis
            redisService.set(GoodsPrefix.getGoodsList,"",html);
        }
        return html;
    }

    /**
     * 准备商品详情数据，跳转到商品详情页面
     * 1. 添加用户信息
     * 2. 从缓存中取页面，取到直接返回页面，取不到开始准备手动渲染
     * 3. 查数据库，查出商品Vo信息，放到model
     * 4. 判断当前时间相对秒杀开始和结束时间的范围，将剩余时间和秒杀状态放入model（如果未开始则倒计时并禁用秒杀按钮，如果已结束则禁用秒杀按钮）
     * 5. 手动渲染页面，如果渲染成功，将页面缓存到Redis，注意存入时要设置有效期
     * 6. 返回html
     * @param user 由统一参数处理器处理得到
     */
    @RequestMapping(value = "/to_detail/{goodsId}",produces = "text/html")
    @ResponseBody
    public String toDetail(HttpServletRequest request, HttpServletResponse response, Model model, User user,@PathVariable("goodsId") Long goodsId){
        model.addAttribute("user", user);

        //在查数据前尝试从Redis缓存中取页面，取到直接返回无需查数据库，取不到再去一步步手动渲染
        String html = redisService.get(GoodsPrefix.getGoodsDetail, "" + goodsId, String.class);
        if (!StringUtils.isEmpty(html)){
            return html;
        }

        GoodsVo goodsVo=goodsService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goodsVo", goodsVo);
        long start=goodsVo.getSecKillStartDate().getTime();
        long end=goodsVo.getSecKillEndDate().getTime();
        long now=System.currentTimeMillis();
        if (start>=end){
            throw new GlobalException(Status.BIND_ERROR.fillArgs("开始时间必须早于结束时间！"));
        }
        //秒杀状态
        int secKillStatus=-1;
        //秒杀剩余多少时间开始
        int remainSeconds=(int)(start-now);
        if (remainSeconds>0){
            //秒杀未开始，倒计时
            secKillStatus=0;
        }else if (end<now){
            //秒杀已结束
            secKillStatus=2;
        }else {
            //秒杀进行中。。。
            secKillStatus=1;
            remainSeconds=0;
        }
        model.addAttribute("secKillStatus",secKillStatus);
        model.addAttribute("remainSeconds", remainSeconds);
        //手动渲染页面
        SpringWebContext springWebContext = new SpringWebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap(), applicationContext);
        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", springWebContext);
       //手动渲染结束，如果渲染成功，将渲染好的页面缓存到Redis
        if (!StringUtils.isEmpty(html)){
            redisService.set(GoodsPrefix.getGoodsDetail, ""+goodsId, html);
        }
        return html;
    }

    /**
     * 获取商品详情数据
     * 1.查询商品详情
     * 2.判断商品秒杀状态
     * 3.将商品详情、秒杀状态、秒杀开始剩余时间都放到GoodsDetailVo对象中返回
     * @param goodsId 商品id
     * @return
     */
    @RequestMapping(value="/detail/{goodsId}")
    @ResponseBody
    public JsonBean<GoodsDetailVo> detail(HttpServletRequest request, HttpServletResponse response, Model model, User user,@PathVariable("goodsId")long goodsId) {
    	GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
    	if (goodsVo==null){
    	    return new JsonBean<>(Status.GOODS_NOT_EXIST);
        }
    	long startTime = goodsVo.getSecKillStartDate().getTime();
    	long endTime = goodsVo.getSecKillEndDate().getTime();
    	long now = System.currentTimeMillis();
    	int secKillStatus;
    	int remainSeconds;
    	if(now < startTime ) {//秒杀还没开始，倒计时
    		secKillStatus = 0;
    		remainSeconds = (int)((startTime - now )/1000);
    	}else  if(now > endTime){//秒杀已经结束
    		secKillStatus = 2;
    		remainSeconds = -1;
    	}else {//秒杀进行中
    		secKillStatus = 1;
    		remainSeconds = 0;
    	}
    	GoodsDetailVo vo = new GoodsDetailVo();
    	vo.setGoodsVo(goodsVo);
    	vo.setUser(user);
    	vo.setRemainSeconds(remainSeconds);
    	vo.setSecKillStatus(secKillStatus);
    	return new JsonBean<>(Status.SUCCESS,vo);
    }

}
