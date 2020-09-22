package com.born.secKill02.service.impl;

import com.born.secKill02.common.Status;
import com.born.secKill02.config.redis.UserPrefix;
import com.born.secKill02.entity.User;
import com.born.secKill02.exception.GlobalException;
import com.born.secKill02.mapper.IUserMapper;
import com.born.secKill02.service.IUserService;
import com.born.secKill02.service.RedisService;
import com.born.secKill02.utils.MD5Util;
import com.born.secKill02.utils.UUIDUtils;
import com.born.secKill02.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description:
 * @Since: jdk1.8
 * @Author: gyk
 * @Date: 2020-04-11 10:42:57
 */
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private IUserMapper userMapper;

    @Autowired
    private RedisService redisService;

    public static final String COOKIE_NAME_TOKEN="token";

    /**
     * 根据用户ID获取用户信息
     * 对象缓存
     * @param userId
     * @return
     */
    @Override
    public User getUserById(Long userId) {
        //尝试从缓存中取用户
        User user = redisService.get(UserPrefix.getByUserId, "" + userId, User.class);
        if (user!=null){
            return user;
        }
        //缓存中没有，从数据库中取
        user = userMapper.getUserById(userId);
        //取出后如果不为空，往缓存中存一份
        if (user!=null){
            redisService.set(UserPrefix.getByUserId,""+userId,user);
        }
        return user;
    }

    /**
     * 更新密码
     * 1.查询用户信息，如果不存在，直接抛全局异常
     * 2.更新数据库
     * 3.更新缓存  先删旧的再设置新的
     * 4.返回true
     *
     *
     * 为什么不能先更新缓存，再更新数据库？ 因为更新缓存时，会将数据库中的旧数据更新到缓存中，导致数据不一致
     * https://blog.csdn.net/tTU1EvLDeLFq5btqiK/article/details/78693323
     * @param token
     * @param userId
     * @param formPass
     * @return
     */
    public boolean updatePassword(String token,long userId ,String formPass){
        User user = getUserById(userId);
        if (user==null){
            throw new GlobalException(Status.MOBILE_NOT_EXIST);
        }
        //新建一个用于更新的用户对象，只设置必需的信息，然后更新数据库
        User userForUpdate = new User();
        userForUpdate.setUserId(userId);
        userForUpdate.setUserPassword(MD5Util.formPassToDBPass(formPass,user.getUserSalt()));;
        userMapper.updatePassword(userForUpdate);
        //更新缓存
        //  删除redis里用户ID对应的用户信息（下次使用会自动存入新的）
        redisService.delete(UserPrefix.getByUserId,""+userId);
        //  先删掉token对应的旧的信息，再用token绑定新信息
        user.setUserPassword(userForUpdate.getUserPassword());
        redisService.set(UserPrefix.token,token, user);
        return true;
    }



    /**
     * 通过token中从分布式Session（Redis）中获取数据,并自动重新续期
     * 续期使用的token还是原来的token
     */
    public User getUserByToken(HttpServletResponse response,String token){
        if (StringUtils.isEmpty(token)){
            return null;
        }
        User user = redisService.get(UserPrefix.token, token, User.class);
        //取出值后，如果该值存在，使用同样的token重新存储设置有效期以达到自动续期的效果
        if (user!=null){
            addUserCookie(response,token,user);
        }
        return user;
    }

    /**
     * 登录
     * 1. 参数合法性验证，失败直接抛出统一全局异常
     * 2. 用户名和密码验证
     * 3. 获取的用户信息存入分布式Session
     * @param response
     * @param loginVo
     * @return
     */
    @Override
    public String login(HttpServletResponse response, LoginVo loginVo) {
        if (loginVo == null) {
            //直接抛出，让统一异常处理器去处理
            throw new GlobalException(Status.SERVER_ERROR);
        }
        System.out.println(loginVo.getPassword());
        //判断手机号是否存在
        User userById = userMapper.getUserById(Long.parseLong(loginVo.getMobile()));
        if (userById == null) {
            throw new GlobalException(Status.MOBILE_NOT_EXIST);
        }
        //验证密码
        //根据表单密码以及数据库盐计算出应在数据库的密码，作比对
        String calcDbPass = MD5Util.formPassToDBPass(loginVo.getPassword(), userById.getUserSalt());
        if (!calcDbPass.equals(userById.getUserPassword())) {
            throw new GlobalException(Status.PASSWORD_ERROR);
        }
        //信息存入分布式session，生成Cookie
        String token = UUIDUtils.uuid();
        addUserCookie(response,token, userById);
        return token;
    }

    /**
     * 将用户信息存入分布式Session--Redis，并生成对应cookie
     * 1. 生成随机的token
     * 2. 将该token加上模块前缀作为key，存的值作为value，存入redis
     * 3. 新建一个Cookie，cookie的名字是取该属性时统一的属性名，值是上面随机生成的token
     * 4. 为这个Cookie设置有效期（也是token对应k-v在redis中的有效期啊），设置路径，并存入HttpServletResponse
     *
     * 分布式Session的原理：a.生成一个随机的token，以这个token为key，将用户信息作为value存到服务端的Redis缓存。b.生成一个Cookie，属性名为一个固定的名字“token”，属性值为Redis中用户信息对应的key，存到客户端并且过期时间和Redis中对应过期时间保持一致。c.这样多个机器的信息统一存到服务器，然后不同的机器通过统一的属性名“token”分别拿到各自的token，取出各自的信息。正如：不同的人都统一拿着银行卡，根据“卡号”属性得到各自的卡号取出各自的钱，而所有卡号-金额信息统一存在银行服务器
     *
     * @param response
     * @param user
     */
    private void addUserCookie(HttpServletResponse response, String token, User user){
        redisService.set(UserPrefix.token, token, user);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
        //cookie和redis中对应的k-v同时过期
        cookie.setMaxAge(UserPrefix.token.getExpireSeconds());
        cookie.setPath("/");
        //将cookie写入客户端
        response.addCookie(cookie);
    }
}
