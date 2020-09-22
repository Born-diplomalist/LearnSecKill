package com.born.secKill02.access;


import com.born.secKill02.entity.User;

public class UserContext {
	
	private static ThreadLocal<User> userHolder = new ThreadLocal<>();
	
	public static void setUser(User user) {
		userHolder.set(user);
	}
	
	public static User getUser() {
		return userHolder.get();
	}

}
