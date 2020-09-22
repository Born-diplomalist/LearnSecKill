package com.born.secKill02.service;

import com.born.secKill02.common.Status;
import com.born.secKill02.config.redis.SecKillPrefix;
import com.born.secKill02.entity.OrderInfo;
import com.born.secKill02.entity.SecKill;
import com.born.secKill02.entity.SecKillOrder;
import com.born.secKill02.entity.User;
import com.born.secKill02.exception.GlobalException;
import com.born.secKill02.mapper.ISecKillMapper;
import com.born.secKill02.utils.MD5Util;
import com.born.secKill02.utils.UUIDUtils;
import com.born.secKill02.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

/**
 * @Description:
 * @Since: jdk1.8
 * @Author: gyk
 * @Date: 2020-04-16 12:12:23
 */
@Service
public class ISecKillService {

    public static final String COOKI_NAME_TOKEN = "token";

    @Autowired
    private ISecKillMapper secKillMapper;

    @Autowired
    private IOrderService orderService;

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private RedisService redisService;

    /**
     * 秒杀核心逻辑
     * <p>
     * <p>
     * 减库存
     * 下订单
     * 写入秒杀订单
     *
     * @param user
     * @param goodsVo
     * @return
     */
    @Transactional
    public OrderInfo secondKill(User user, GoodsVo goodsVo) {
        //此处添加了事务，那么还需要判断执行结果么
        if (secKillMapper.reduceStock(goodsVo) > 0) {
            return orderService.createOrder(user, goodsVo);
        } else {
            //减库存失败--商品售空---设置表示
            setGoodsOver(goodsVo.getGoodsId());
            return null;
        }
    }


    /**
     * 获取秒杀结果（生成的订单ID）
     * <p>
     * 如果生成了订单，认为秒杀成功，返回订单ID
     * 如果未生成，判断剩余的库存是否售完
     *
     * @param userId
     * @param goodsId
     * @return 0表示未售完，-1表示已售完，其他的表示生成的订单ID
     */
    public long getSecKillResult(Long userId, long goodsId) {
        SecKillOrder secKillOrder = orderService.getSecKillOrderByUserIdAndGoodsId(userId, goodsId);
        if (secKillOrder != null) {//秒杀成功
            Long secKillOrderId = secKillOrder.getSecKillOrderId();
            if (null!=secKillOrderId){
                return secKillOrderId;
            }else {
                throw new GlobalException(Status.BIND_ERROR.fillArgs("秒杀订单ID为null"));
            }
        } else {
            boolean isOver = getGoodsOver(goodsId);
            if (isOver) {
                return -1;
            } else {
                return 0;
            }
        }
    }


    /**
     * 重置商品库存
     * 删除已有订单和订单详情
     * 之后需要删除订单
     *
     * @param goodsVoList
     */
    public void reset(List<GoodsVo> goodsVoList) {
        resetStock(goodsVoList);
        orderService.cleanOrder();
    }

    /**
     * 验证访问的path是否和预先设定的一致
     *
     * @param user
     * @param goodsId
     * @param path
     * @return
     */
    public boolean checkPath(User user, Long goodsId, String path) {
        if (user == null || path == null) {
            return false;
        }
        String pathOld = redisService.get(SecKillPrefix.getSecKillPath, "" + user.getUserId() + "_" + goodsId, String.class);
        return path.equals(pathOld);
    }


    public String createSecKillPath(User user, long goodsId) {
        if (user == null || goodsId <= 0) {
            return null;
        }
        String str = MD5Util.md5(UUIDUtils.uuid() + "123456");
        redisService.set(SecKillPrefix.getSecKillPath, "" + user.getUserId() + "_" + goodsId, str);
        return str;
    }


    //重置库存
    void resetStock(List<GoodsVo> goodsVoList) {
        for (GoodsVo goods : goodsVoList) {
            SecKill secKill = new SecKill();
            secKill.setSecKillGoodsId(goods.getGoodsId());
            //重置秒杀库存为设定的商品数量（注意此处不是秒杀数量）
            secKill.setSecKillStock(goods.getGoodsStock());
            secKillMapper.resetStock(secKill);
        }
    }

    public SecKill getSecKillByGoodsId(long goodsId) {
        return secKillMapper.getSecKillByGoodsId(goodsId);
    }


    //将指定商品在redis中设为售空
    private void setGoodsOver(Long goodsId) {
        redisService.set(SecKillPrefix.isGoodsOver, "" + goodsId, true);
    }

    //获知是否该商品在redis中售空
    private boolean getGoodsOver(long goodsId) {
        return redisService.exists(SecKillPrefix.isGoodsOver, "" + goodsId);
    }

    /**
     * 创建验证码
     * @param user
     * @param goodsId
     * @return
     */
    public BufferedImage createVerifyCode(User user, long goodsId) {
		if(user == null || goodsId <=0) {
			return null;
		}
		int width = 80;
		int height = 32;
		//create the image
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics g = image.getGraphics();
		// set the background color
		g.setColor(new Color(0xDCDCDC));
		g.fillRect(0, 0, width, height);
		// draw the border
		g.setColor(Color.black);
		g.drawRect(0, 0, width - 1, height - 1);
		// create a random instance to generate the codes
		Random rdm = new Random();
		// make some confusion
		for (int i = 0; i < 50; i++) {
			int x = rdm.nextInt(width);
			int y = rdm.nextInt(height);
			g.drawOval(x, y, 0, 0);
		}
		// generate a random code
		String verifyCode = generateVerifyCode(rdm);
		g.setColor(new Color(0, 100, 0));
		g.setFont(new Font("Candara", Font.BOLD, 24));
		g.drawString(verifyCode, 8, 24);
		g.dispose();
		//把验证码存到redis中
		int rnd = calc(verifyCode);
		redisService.set(SecKillPrefix.getSecKillVerifyCode,  user.getUserId()+","+goodsId, rnd);
		//redisService.set(SecKillPrefix.getSecKillPath,  user.getUserId()+","+goodsId, rnd);
		//输出图片
		return image;
	}

    /**
     * 校验验证码
     * @param user
     * @param goodsId
     * @param verifyCode
     * @return
     */
	public boolean checkVerifyCode(User user, long goodsId, int verifyCode) {
		if(user == null || goodsId <=0) {
			return false;
		}
		Integer codeOld = redisService.get(SecKillPrefix.getSecKillVerifyCode, user.getUserId()+","+goodsId, Integer.class);
		if(codeOld == null || codeOld - verifyCode != 0 ) {
			return false;
		}
		redisService.delete(SecKillPrefix.getSecKillVerifyCode, user.getUserId()+","+goodsId);
		return true;
	}

    /**
     * 计算表达式
     * @param exp
     * @return
     */
	private static int calc(String exp) {
		try {
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine engine = manager.getEngineByName("JavaScript");
			return (Integer)engine.eval(exp);
		}catch(Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	private static char[] ops = new char[] {'+', '-', '*'};
	/**
	 * + - *
	 * */
	private String generateVerifyCode(Random rdm) {
		int num1 = rdm.nextInt(10);
	    int num2 = rdm.nextInt(10);
		int num3 = rdm.nextInt(10);
		char op1 = ops[rdm.nextInt(3)];
		char op2 = ops[rdm.nextInt(3)];
		String exp = ""+ num1 + op1 + num2 + op2 + num3;
		return exp;
	}

}
