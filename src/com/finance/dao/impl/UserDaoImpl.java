package com.finance.dao.impl;

import com.finance.dao.UserDao;
import com.finance.entity.User;
import com.finance.util.DBUtil;

import java.sql.*;

public class UserDaoImpl implements UserDao {

    // 在 UserDaoImpl.java 中添加以下方法实现

    @Override
    public boolean delete(Integer userId, String password) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // 首先验证密码
            if (!checkPassword(userId, password)) {
                return false;
            }

            conn = DBUtil.getConnection();
            // 开启事务
            conn.setAutoCommit(false);

            // 1. 删除用户的账单记录
            String deleteBillsSql = "DELETE FROM bill WHERE user_id = ?";
            pstmt = conn.prepareStatement(deleteBillsSql);
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
            pstmt.close();

            // 2. 删除用户的自定义分类
            String deleteCategoriesSql = "DELETE FROM category WHERE user_id = ?";
            pstmt = conn.prepareStatement(deleteCategoriesSql);
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
            pstmt.close();

            // 3. 删除用户账户
            String deleteUserSql = "DELETE FROM user WHERE id = ?";
            pstmt = conn.prepareStatement(deleteUserSql);
            pstmt.setInt(1, userId);
            int result = pstmt.executeUpdate();

            // 提交事务
            conn.commit();
            return result > 0;

        } catch (SQLException e) {
            // 回滚事务
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            DBUtil.close(conn, pstmt);
        }
    }

    @Override
    public boolean checkPassword(Integer userId, String password) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT password FROM user WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                return storedPassword.equals(password);
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    @Override
    public boolean register(User user) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "INSERT INTO user(username, password, nickname) VALUES(?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getNickname());

            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(conn, pstmt);
        }
    }

    @Override
    public User login(String username, String password) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM user WHERE username = ? AND password = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setNickname(rs.getString("nickname"));
                user.setCreateTime(rs.getTimestamp("create_time"));
                user.setUpdateTime(rs.getTimestamp("update_time"));
                return user;
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    @Override
    public User findById(Integer id) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM user WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setNickname(rs.getString("nickname"));
                user.setCreateTime(rs.getTimestamp("create_time"));
                user.setUpdateTime(rs.getTimestamp("update_time"));
                return user;
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    @Override
    public User findByUsername(String username) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM user WHERE username = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setNickname(rs.getString("nickname"));
                user.setCreateTime(rs.getTimestamp("create_time"));
                user.setUpdateTime(rs.getTimestamp("update_time"));
                return user;
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    @Override
    public boolean update(User user) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "UPDATE user SET password = ?, nickname = ? WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.getPassword());
            pstmt.setString(2, user.getNickname());
            pstmt.setInt(3, user.getId());

            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(conn, pstmt);
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT COUNT(*) FROM user WHERE username = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }
}