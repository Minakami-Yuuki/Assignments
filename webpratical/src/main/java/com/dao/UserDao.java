package com.dao;

import com.Info.User;
import com.model.BasicDao;

/**
 * @author: Mr.Yu
 * @create: 2021-09-14 21:20
 **/
public class UserDao extends BasicDao<User> {

    public User login(String username,String password){
        String sql = "select * from user where username=? and password=?";

        User user = querySingle(sql, User.class, username, password);
        return user;
    }

}
