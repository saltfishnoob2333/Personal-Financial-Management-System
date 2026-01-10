package com.finance.dao.impl;

import com.finance.dao.CategoryDao;
import com.finance.entity.Category;
import com.finance.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDaoImpl implements CategoryDao {

    @Override
    public List<Category> getSystemCategories() {
        return getCategoriesByType(0, null);
    }

    @Override
    public List<Category> getUserCategories(Integer userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Category> categories = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM category WHERE user_id = ? OR user_id = 0 ORDER BY user_id, type, name";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Category category = new Category();
                category.setId(rs.getInt("id"));
                category.setName(rs.getString("name"));
                category.setType(rs.getString("type"));
                category.setUserId(rs.getInt("user_id"));
                category.setCreateTime(rs.getTimestamp("create_time"));
                categories.add(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return categories;
    }

    @Override
    public List<Category> getCategoriesByType(Integer userId, String type) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Category> categories = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            StringBuilder sql = new StringBuilder(
                    "SELECT * FROM category WHERE (user_id = ? OR user_id = 0)");

            if (type != null && !type.isEmpty()) {
                sql.append(" AND type = ?");
            }
            sql.append(" ORDER BY user_id, name");

            pstmt = conn.prepareStatement(sql.toString());
            pstmt.setInt(1, userId);

            if (type != null && !type.isEmpty()) {
                pstmt.setString(2, type);
            }

            rs = pstmt.executeQuery();

            while (rs.next()) {
                Category category = new Category();
                category.setId(rs.getInt("id"));
                category.setName(rs.getString("name"));
                category.setType(rs.getString("type"));
                category.setUserId(rs.getInt("user_id"));
                category.setCreateTime(rs.getTimestamp("create_time"));
                categories.add(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return categories;
    }

    @Override
    public boolean add(Category category) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "INSERT INTO category(name, type, user_id) VALUES(?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, category.getName());
            pstmt.setString(2, category.getType());
            pstmt.setInt(3, category.getUserId());

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
    public boolean update(Category category) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "UPDATE category SET name = ?, type = ? WHERE id = ? AND user_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, category.getName());
            pstmt.setString(2, category.getType());
            pstmt.setInt(3, category.getId());
            pstmt.setInt(4, category.getUserId());

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
    public boolean delete(Integer id, Integer userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "DELETE FROM category WHERE id = ? AND user_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.setInt(2, userId);

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
    public Category findById(Integer id) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM category WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                Category category = new Category();
                category.setId(rs.getInt("id"));
                category.setName(rs.getString("name"));
                category.setType(rs.getString("type"));
                category.setUserId(rs.getInt("user_id"));
                category.setCreateTime(rs.getTimestamp("create_time"));
                return category;
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
    public boolean exists(String name, Integer userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT COUNT(*) FROM category WHERE name = ? AND user_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setInt(2, userId);
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