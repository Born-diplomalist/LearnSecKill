package com.born.secKill02.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 枚举缺点之一：不能根据参数改变枚举对象
 *
 * 此处应考虑使用Java8的流式计算特性，使用setter改变参数后还能返回原对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Status {

    private int code;
	private String msg;

	//通用的状态码
	public static Status SUCCESS = new Status(0, "success");
	public static Status SERVER_ERROR = new Status(500100, "服务端异常");
	public static Status BIND_ERROR = new Status(500101, "参数校验异常：%s");
	public static final Status REQUEST_INLEGAL = new Status(500102, "非法请求	");
    public static final Status ACCESS_LIMIT_REACHED = new Status(500103, "访问过于频繁！	");;
	//登录模块 5002XX
	public static Status SESSION_ERROR = new Status(500210, "Session不存在或者已经失效");
	public static Status PASSWORD_EMPTY = new Status(500211, "登录密码不能为空");
	public static Status MOBILE_EMPTY = new Status(500212, "手机号不能为空");
	public static Status MOBILE_ERROR = new Status(500213, "手机号格式错误");
	public static Status MOBILE_NOT_EXIST = new Status(500214, "手机号不存在");
	public static Status PASSWORD_ERROR = new Status(500215, "密码错误");

	//商品模块 5003XX
	public static Status GOODS_NOT_EXIST =new Status(500300,"商品不存在");

	//订单模块 5004XX
	public static Status ORDER_NOT_EXIST =new Status(500400,"订单不存在");
	public static Status ORDER_HAS_EXIST =new Status(500401,"订单已存在");

	//秒杀模块 5005XX
    public static Status SECKILL_HAS_OVER =new Status(500500,"秒杀已经结束");
    public static Status SECKILL_HAS_NOT_START =new Status(500501,"秒杀未开始");
    public static Status SECKILL_RUNNING =new Status(500502,"秒杀进行中");
    public static Status SECKILL_REPEAT =new Status(500503,"不允许重复秒杀");
    public static Status SECKILL_SELL_OVER =new Status(500503,"秒杀商品已售空");
    public static Status SECKILL_FAIL =new Status(500504,"秒杀失败");


	public Status fillArgs(Object... args) {
		int code = this.code;
		String message = String.format(this.msg, args);
        return new Status(code, message);
	}

}
