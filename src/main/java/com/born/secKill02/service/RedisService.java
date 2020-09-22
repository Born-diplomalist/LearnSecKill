package com.born.secKill02.service;

import com.alibaba.fastjson.JSON;
import com.born.secKill02.config.redis.KeyPrefix;
import com.born.secKill02.config.redis.SecKillPrefix;
import com.born.secKill02.config.redis.UserPrefix;
import com.born.secKill02.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Since: jdk1.8
 * @Author: gyk
 * @Date: 2020-04-09 20:30:41
 */
@Service
public class RedisService {

    @Autowired
    private JedisPool jedisPool;

    /**
     * 获取指定key的value
     * 1.获取jedis对象
     * 2.算出真正存在于Redis的对应key值
     * 3.使用这个真实的key取出value，并进行类型转换
     * 4.将jedis归还到连接池
     *
     * @param prefix key的前缀，用于区分不同模块的同名key
     * @return 指定key的value
     */
    public <T> T get(KeyPrefix prefix, String key, Class<T> clazz) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            //得到真正的key
            String realKey = prefix.getPrefix() + key;
            String keyStr = jedis.get(realKey);
            return JsonUtil.stringToBean(keyStr, clazz);
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * 设置指定k-v
     * 1.获取jedis对象
     * 2.将value转换为字符串，并验证字符串
     * 3.算出真实的key
     * 4.获取超时时间，如果超时时间大于0，说明有超时时间，使用带超时时间的API设置k-v；
     *   否则使用不带时间的API设置k-v
     * 5.将jedis对象归还到连接池
     * @return 是否设置成功
     */
    public <T> boolean set(KeyPrefix prefix, String key, T value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String valueStr = JsonUtil.beanToString(value);
            if (valueStr == null || valueStr.length() <= 0) {
                return false;
            }
            String realKey = prefix.getPrefix() + key;
            int seconds = prefix.getExpireSeconds();
            if (seconds <= 0) {
                jedis.set(realKey, valueStr);
            } else {
                //set和expire的整合 setEx
                jedis.setex(realKey, seconds, valueStr);
            }
            return true;
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * 验证key是否存在
     * 1.获取jedis
     * 2.获取真实的key
     * 3.用真实的key去查询key是否存在
     * 4.归还redis到连接池
     * @return 存在返回true  不存在返回false
     */
    public <T> boolean exists(KeyPrefix prefix,String key){
        Jedis jedis=null;
        try {
            jedis=jedisPool.getResource();
            String realKey=prefix.getPrefix()+key;
            return jedis.exists(realKey);
        }finally {
            returnToPool(jedis);
        }
    }


    /**
     * 自增  原子操作
     * 1.获取jedis
     * 2.获取真实key
     * 3.自增
     * 4.归还jedis到连接池
     * @return 自增后的值
     */
    public <T> Long incr(KeyPrefix prefix,String key){
        Jedis jedis=null;
        try{
            jedis=jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            return jedis.incr(realKey);
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 自减  原子操作
     * 1.获取jedis对象
     * 2.获取真实key
     * 3.执行自减操作
     * 4.将jedis归还到连接池
     * @return 自减后的值
     */
    public <T> Long decr(KeyPrefix prefix,String key){
        Jedis jedis=null;
        try {
            jedis= jedisPool.getResource();
            String realKey=prefix.getPrefix()+key;
            return jedis.incr(realKey);
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 对象放回连接池
     */
    private void returnToPool(Jedis jedis) {
        if (jedis != null) jedis.close();
    }


    public boolean delete(KeyPrefix prefix, String key) {
        		 Jedis jedis = null;
		 try {
			 jedis =  jedisPool.getResource();
			//生成真正的key
			String realKey  = prefix.getPrefix() + key;
			long ret =  jedis.del(realKey);
			return ret > 0;
		 }finally {
			  returnToPool(jedis);
		 }
    }

    public boolean delete(KeyPrefix prefix) {
        		if(prefix == null) {
			return false;
		}
		List<String> keys = scanKeys(prefix.getPrefix());
		if(keys==null || keys.size() <= 0) {
			return true;
		}
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.del(keys.toArray(new String[0]));
			return true;
		} catch (final Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if(jedis != null) {
				jedis.close();
			}
		}
    }

    public List<String> scanKeys(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			List<String> keys = new ArrayList<String>();
			String cursor = "0";
			ScanParams sp = new ScanParams();
			sp.match("*"+key+"*");
			sp.count(100);
			do{
				ScanResult<String> ret = jedis.scan(cursor, sp);
				List<String> result = ret.getResult();
				if(result!=null && result.size() > 0){
					keys.addAll(result);
				}
				//再处理cursor
				cursor = ret.getStringCursor();
			}while(!cursor.equals("0"));
			return keys;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public static <T> String beanToString(T value) {
		if(value == null) {
			return null;
		}
		Class<?> clazz = value.getClass();
		if(clazz == int.class || clazz == Integer.class) {
			 return ""+value;
		}else if(clazz == String.class) {
			 return (String)value;
		}else if(clazz == long.class || clazz == Long.class) {
			return ""+value;
		}else {
			return JSON.toJSONString(value);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T stringToBean(String str, Class<T> clazz) {
		if(str == null || str.length() <= 0 || clazz == null) {
			 return null;
		}
		if(clazz == int.class || clazz == Integer.class) {
			 return (T)Integer.valueOf(str);
		}else if(clazz == String.class) {
			 return (T)str;
		}else if(clazz == long.class || clazz == Long.class) {
			return  (T)Long.valueOf(str);
		}else {
			return JSON.toJavaObject(JSON.parseObject(str), clazz);
		}
	}



}











