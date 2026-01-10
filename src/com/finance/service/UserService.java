package com.finance.service;

import com.finance.dao.UserDao;
import com.finance.dao.impl.UserDaoImpl;
import com.finance.entity.User;

public class UserService {
    private UserDao userDao = new UserDaoImpl();

    /**
     * 用户注册
     */
    public boolean register(User user) {
        // 检查用户名是否已存在
        if (userDao.existsByUsername(user.getUsername())) {
            return false;
        }

        // 可以在这里添加密码加密逻辑
        // user.setPassword(encryptPassword(user.getPassword()));

        return userDao.register(user);
    }

    /**
     * 用户登录
     */
    public User login(String username, String password) {
        // 可以在这里添加密码验证逻辑
        // password = encryptPassword(password);

        return userDao.login(username, password);
    }

    /**
     * 更新用户信息
     */
    public boolean updateUser(User user) {
        return userDao.update(user);
    }

    /**
     * 根据ID获取用户
     */
    public User getUserById(Integer id) {
        return userDao.findById(id);
    }

    /**
     * 检查用户名是否存在
     */
    public boolean checkUsernameExists(String username) {
        return userDao.existsByUsername(username);
    }

    // 简单的密码加密方法（示例）
    private String encryptPassword(String password) {
        // 实际项目中应该使用更安全的加密方式，如BCrypt
        return password; // 这里返回原密码，实际应该加密
    }

    // 在 UserService.java 中添加以下方法

    /**
     * 删除用户账户
     */
    public boolean deleteAccount(Integer userId, String password) {
        // 验证密码
        User user = userDao.findById(userId);
        if (user == null) {
            return false;
        }

        // 可以在这里添加额外的业务逻辑，比如检查是否有未处理的账单等

        return userDao.delete(userId, password);
    }

    /**
     * 验证密码
     */
    public boolean verifyPassword(Integer userId, String password) {
        return userDao.checkPassword(userId, password);
    }
}