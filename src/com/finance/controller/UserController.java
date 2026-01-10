package com.finance.controller;

import com.finance.entity.User;
import com.finance.service.UserService;

public class UserController {
    private UserService userService = new UserService();
    private static User currentUser; // 当前登录用户

    /**
     * 用户注册
     */
    public boolean register(String username, String password, String nickname) {
        User user = new User(username, password, nickname);
        return userService.register(user);
    }

    /**
     * 用户登录
     */
    public boolean login(String username, String password) {
        User user = userService.login(username, password);
        if (user != null) {
            currentUser = user;
            return true;
        }
        return false;
    }

    /**
     * 用户注销
     */
    public void logout() {
        currentUser = null;
    }

    /**
     * 更新用户信息
     */
    public boolean updateUser(String password, String nickname) {
        if (currentUser == null) {
            return false;
        }

        currentUser.setPassword(password);
        currentUser.setNickname(nickname);

        boolean success = userService.updateUser(currentUser);
        if (success) {
            // 重新获取最新用户信息
            currentUser = userService.getUserById(currentUser.getId());
        }
        return success;
    }

    /**
     * 获取当前用户
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * 检查是否已登录
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * 检查用户名是否存在
     */
    public boolean checkUsernameExists(String username) {
        return userService.checkUsernameExists(username);
    }

    // 在 UserController.java 中添加以下方法

    /**
     * 删除当前用户账户
     */
    public boolean deleteAccount(String password) {
        if (!isLoggedIn()) {
            return false;
        }

        // 验证密码
        if (!userService.verifyPassword(currentUser.getId(), password)) {
            return false;
        }

        boolean success = userService.deleteAccount(currentUser.getId(), password);
        if (success) {
            // 删除成功后注销当前用户
            logout();
        }
        return success;
    }

    /**
     * 验证密码
     */
    public boolean verifyPassword(String password) {
        if (!isLoggedIn()) {
            return false;
        }
        return userService.verifyPassword(currentUser.getId(), password);
    }
}