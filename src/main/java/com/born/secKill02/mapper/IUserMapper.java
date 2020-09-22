package com.born.secKill02.mapper;

import com.born.secKill02.entity.User;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * @Since: jdk1.8
 * @Author: gyk
 * @Date: 2020-04-11 09:50:55
 */
@Mapper
public interface IUserMapper {


    @Select("select user_id as userId,user_nick_name as userNickName,user_password as userPassword,user_salt as userSalt,user_head as userHead,user_register_date as userRegisterDate,user_last_login_date as userLastLoginDate,user_login_count as userLoginCount from user where user_id=#{userId}")
    //User getUserById(Long userId);
    User getUserById(@Param("userId")Long userId);

    //@Insert("insert into user(user_id, user_name)values(#{userId}, #{userNickName})")
	//int insert(User user);

    //@Insert("insert into user(login_count, nickname, register_date, salt, password, id)values(?,?,?,?,?,?)")
    //User insertUser(Long userId);

    @Update("update user set password = #{password} where id = #{id}")
    void updatePassword(User userForUpdate);
}







