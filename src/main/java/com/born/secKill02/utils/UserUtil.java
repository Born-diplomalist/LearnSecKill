package com.born.secKill02.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.born.secKill02.entity.User;

public class UserUtil {
	
	private static void createUser(int count) throws Exception{
		List<User> userList = new ArrayList<User>(count);
		//生成用户
		for(int i=0;i<count;i++) {
            User user = new User();
            user.setUserId(1300000000000L+i);
            user.setUserNickName("user"+i);
            user.setUserSalt("a43gsa");
            user.setUserPassword(MD5Util.inputPassToDbPass("123456", user.getUserSalt()));
            user.setUserRegisterDate(new Date());
            user.setUserLoginCount(1);
            userList.add(user);
		}
		System.out.println("create user");
//		//插入数据库
		Connection conn = DBUtil.getConn();
		String sql = "insert into user(user_id,user_nick_name,user_password,user_salt,user_register_date,user_login_count)values(?,?,?,?,?,?)";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		for(int i=0;i<userList.size();i++) {
			User user = userList.get(i);
			pstmt.setLong(1, user.getUserId());
			pstmt.setString(2, user.getUserNickName());
			pstmt.setString(3, user.getUserPassword());
			pstmt.setString(4, user.getUserSalt());
			pstmt.setTimestamp(5, new Timestamp(user.getUserRegisterDate().getTime()));
			pstmt.setInt(6, user.getUserLoginCount());
			pstmt.addBatch();
		}
		pstmt.executeBatch();
		pstmt.close();
		conn.close();
		System.out.println("insert to db");
		//登录，生成token
		String urlString = "http://localhost:8080/login/do_login";
		File file = new File("D:/tokens.txt");
		if(file.exists()) {
			file.delete();
		}
		RandomAccessFile raf = new RandomAccessFile(file, "rw");
		file.createNewFile();
		raf.seek(0);
		for(int i=0;i<userList.size();i++) {
			User user = userList.get(i);
			URL url = new URL(urlString);
			HttpURLConnection co = (HttpURLConnection)url.openConnection();
			co.setRequestMethod("POST");
			co.setDoOutput(true);
			OutputStream out = co.getOutputStream();
			String params = "mobile="+user.getUserId()+"&password="+MD5Util.inputPassToFormPass("123456");
			out.write(params.getBytes());
			out.flush();
			InputStream inputStream = co.getInputStream();
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			byte buff[] = new byte[1024];
			int len = 0;
			while((len = inputStream.read(buff)) >= 0) {
				bout.write(buff, 0 ,len);
			}
			inputStream.close();
			bout.close();
			String response = new String(bout.toByteArray());
			JSONObject jo = JSON.parseObject(response);
			String token = jo.getString("data");
			System.out.println("create token : " + user.getUserId());
			
			String row = user.getUserId()+","+token;
			raf.seek(raf.length());
			raf.write(row.getBytes());
			raf.write("\r\n".getBytes());
			System.out.println("write to file : " + user.getUserId());
		}
		raf.close();
		
		System.out.println("over");
	}
	
	public static void main(String[] args)throws Exception {
		createUser(5000);
	}
}
