package com.databrew.cafe.service;

import com.databrew.cafe.dao.UserDao;
import com.databrew.cafe.model.User;
import com.databrew.cafe.util.PasswordUtil;

public class AuthService {
    private final UserDao userDao = new UserDao();

    public User authenticate(String username, String plainPassword) throws Exception {
        User user = userDao.findByUsername(username);
        if (user == null || !user.isActive()) {
            return null;
        }
        if (!PasswordUtil.verify(plainPassword, user.getPasswordHash())) {
            return null;
        }
        return user;
    }
}
