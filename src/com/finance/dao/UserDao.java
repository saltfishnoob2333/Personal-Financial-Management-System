package com.finance.dao;

import com.finance.entity.User;

public interface UserDao {
    // 用户注册
    boolean register(User user);

    // 在 UserDao.java 中添加以下方法

    /**
     * 删除用户账户
     * @param userId 用户ID
     * @param password 密码验证
     * @return 是否删除成功
     */
    boolean delete(Integer userId, String password);

    /**
     * 检查用户密码是否正确
     * @param userId 用户ID
     * @param password 密码
     * @return 密码是否正确
     */
    boolean checkPassword(Integer userId, String password);

    // 用户登录
    User login(String username, String password);

    // 根据ID查询用户
    User findById(Integer id);

    // 根据用户名查询用户
    User findByUsername(String username);

    // 更新用户信息
    boolean update(User user);

    // 检查用户名是否存在
    boolean existsByUsername(String username);
}