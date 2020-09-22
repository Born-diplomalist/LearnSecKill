package com.born.secKill02.rabbitmq;

import com.born.secKill02.config.redis.OrderPrefix;
import com.born.secKill02.entity.SecKill;
import com.born.secKill02.entity.SecKillOrder;
import com.born.secKill02.entity.User;
import com.born.secKill02.service.IGoodsService;
import com.born.secKill02.service.IOrderService;
import com.born.secKill02.service.ISecKillService;
import com.born.secKill02.service.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class MQReceiver {

		private static Logger log = LoggerFactory.getLogger(MQReceiver.class);
		
		@Autowired
		RedisService redisService;
		
		@Autowired
		IGoodsService goodsService;
		
		@Autowired
		IOrderService orderService;
		
		@Autowired
		ISecKillService iSecKillService;
		
		@RabbitListener(queues=MQConfig.TOPIC_QUEUE1)
		public void receive(String message) {
			log.info("receive message:"+message);
			SecKillMessage mm  = RedisService.stringToBean(message, SecKillMessage.class);
			User user = mm.getUser();
			long goodsId = mm.getGoodsId();
			//验证数据库库存是否充足
			SecKill secKillByGoodsId = iSecKillService.getSecKillByGoodsId(goodsId);
	    	int stock = secKillByGoodsId.getSecKillStock();
	    	if(stock <= 0) {
	    		return;
	    	}
	    	//判断是否已经秒杀到了--数据库中是否已有订单记录
	    	SecKillOrder order = orderService.getSecKillOrderByUserIdAndGoodsId(user.getUserId(), goodsId);
	    	if(order != null) {
	    		return;
	    	}
	    	//减库存 下订单 写入秒杀订单
	    	iSecKillService.secondKill(user, goodsService.getGoodsVoByGoodsId(goodsId));
		}
	
//		@RabbitListener(queues=MQConfig.QUEUE)
//		public void receive(String message) {
//			log.info("receive message:"+message);
//		}
//		
//		@RabbitListener(queues=MQConfig.TOPIC_QUEUE1)
//		public void receiveTopic1(String message) {
//			log.info(" topic  queue1 message:"+message);
//		}
//		
//		@RabbitListener(queues=MQConfig.TOPIC_QUEUE2)
//		public void receiveTopic2(String message) {
//			log.info(" topic  queue2 message:"+message);
//		}
//		
//		@RabbitListener(queues=MQConfig.HEADER_QUEUE)
//		public void receiveHeaderQueue(byte[] message) {
//			log.info(" header  queue message:"+new String(message));
//		}
//		
		
}
