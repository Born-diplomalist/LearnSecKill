package com.born.secKill02.rabbitmq;

import com.born.secKill02.entity.User;

public class SecKillMessage {
	private User user;
	private long goodsId;
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public long getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(long goodsId) {
		this.goodsId = goodsId;
	}
}
