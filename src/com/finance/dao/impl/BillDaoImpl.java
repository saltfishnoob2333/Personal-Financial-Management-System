package com.finance.dao.impl;

import com.finance.dao.BillDao;
import com.finance.entity.Bill;
import com.finance.util.DBUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.Date;

public class BillDaoImpl implements BillDao {

    @Override
    public boolean add(Bill bill) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "INSERT INTO bill(user_id, category_id, amount, type, remark, bill_date) " +
                    "VALUES(?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, bill.getUserId());
            pstmt.setInt(2, bill.getCategoryId());
            pstmt.setBigDecimal(3, bill.getAmount());
            pstmt.setString(4, bill.getType());
            pstmt.setString(5, bill.getRemark());
            pstmt.setDate(6, new java.sql.Date(bill.getBillDate().getTime()));

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
    public boolean update(Bill bill) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "UPDATE bill SET category_id = ?, amount = ?, type = ?, " +
                    "remark = ?, bill_date = ? WHERE id = ? AND user_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, bill.getCategoryId());
            pstmt.setBigDecimal(2, bill.getAmount());
            pstmt.setString(3, bill.getType());
            pstmt.setString(4, bill.getRemark());
            pstmt.setDate(5, new java.sql.Date(bill.getBillDate().getTime()));
            pstmt.setInt(6, bill.getId());
            pstmt.setInt(7, bill.getUserId());

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
            String sql = "DELETE FROM bill WHERE id = ? AND user_id = ?";
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
    public Bill findById(Integer id, Integer userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT b.*, c.name as category_name FROM bill b " +
                    "LEFT JOIN category c ON b.category_id = c.id " +
                    "WHERE b.id = ? AND b.user_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.setInt(2, userId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                Bill bill = new Bill();
                bill.setId(rs.getInt("id"));
                bill.setUserId(rs.getInt("user_id"));
                bill.setCategoryId(rs.getInt("category_id"));
                bill.setCategoryName(rs.getString("category_name"));
                bill.setAmount(rs.getBigDecimal("amount"));
                bill.setType(rs.getString("type"));
                bill.setRemark(rs.getString("remark"));
                bill.setBillDate(rs.getDate("bill_date"));
                bill.setCreateTime(rs.getTimestamp("create_time"));
                bill.setUpdateTime(rs.getTimestamp("update_time"));
                return bill;
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
    public List<Bill> findBills(Integer userId, Date startDate, Date endDate,
                                String type, Integer categoryId, int page, int pageSize) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Bill> bills = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            StringBuilder sql = new StringBuilder(
                    "SELECT b.*, c.name as category_name FROM bill b " +
                            "LEFT JOIN category c ON b.category_id = c.id " +
                            "WHERE b.user_id = ?");

            List<Object> params = new ArrayList<>();
            params.add(userId);

            if (startDate != null) {
                sql.append(" AND b.bill_date >= ?");
                params.add(new java.sql.Date(startDate.getTime()));
            }

            if (endDate != null) {
                sql.append(" AND b.bill_date <= ?");
                params.add(new java.sql.Date(endDate.getTime()));
            }

            if (type != null && !type.isEmpty()) {
                sql.append(" AND b.type = ?");
                params.add(type);
            }

            if (categoryId != null && categoryId > 0) {
                sql.append(" AND b.category_id = ?");
                params.add(categoryId);
            }

            sql.append(" ORDER BY b.bill_date DESC, b.create_time DESC ");
            sql.append(" LIMIT ? OFFSET ?");

            params.add(pageSize);
            params.add((page - 1) * pageSize);

            pstmt = conn.prepareStatement(sql.toString());

            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            rs = pstmt.executeQuery();

            while (rs.next()) {
                Bill bill = new Bill();
                bill.setId(rs.getInt("id"));
                bill.setUserId(rs.getInt("user_id"));
                bill.setCategoryId(rs.getInt("category_id"));
                bill.setCategoryName(rs.getString("category_name"));
                bill.setAmount(rs.getBigDecimal("amount"));
                bill.setType(rs.getString("type"));
                bill.setRemark(rs.getString("remark"));
                bill.setBillDate(rs.getDate("bill_date"));
                bill.setCreateTime(rs.getTimestamp("create_time"));
                bill.setUpdateTime(rs.getTimestamp("update_time"));
                bills.add(bill);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return bills;
    }

    @Override
    public int countBills(Integer userId, Date startDate, Date endDate,
                          String type, Integer categoryId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            StringBuilder sql = new StringBuilder(
                    "SELECT COUNT(*) FROM bill WHERE user_id = ?");

            List<Object> params = new ArrayList<>();
            params.add(userId);

            if (startDate != null) {
                sql.append(" AND bill_date >= ?");
                params.add(new java.sql.Date(startDate.getTime()));
            }

            if (endDate != null) {
                sql.append(" AND bill_date <= ?");
                params.add(new java.sql.Date(endDate.getTime()));
            }

            if (type != null && !type.isEmpty()) {
                sql.append(" AND type = ?");
                params.add(type);
            }

            if (categoryId != null && categoryId > 0) {
                sql.append(" AND category_id = ?");
                params.add(categoryId);
            }

            pstmt = conn.prepareStatement(sql.toString());

            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    @Override
    public Map<String, BigDecimal> getMonthlySummary(Integer userId, int year, int month) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Map<String, BigDecimal> summary = new HashMap<>();

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT type, SUM(amount) as total FROM bill " +
                    "WHERE user_id = ? AND YEAR(bill_date) = ? AND MONTH(bill_date) = ? " +
                    "GROUP BY type";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, year);
            pstmt.setInt(3, month);
            rs = pstmt.executeQuery();

            BigDecimal income = BigDecimal.ZERO;
            BigDecimal expense = BigDecimal.ZERO;

            while (rs.next()) {
                String type = rs.getString("type");
                BigDecimal total = rs.getBigDecimal("total");

                if ("INCOME".equals(type)) {
                    income = total;
                } else if ("EXPENSE".equals(type)) {
                    expense = total;
                }
            }

            summary.put("income", income);
            summary.put("expense", expense);
            summary.put("balance", income.subtract(expense));

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return summary;
    }

    @Override
    public List<Map<String, Object>> getCategoryStats(Integer userId, Date startDate, Date endDate, String type) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Map<String, Object>> stats = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT c.name as category_name, SUM(b.amount) as total " +
                    "FROM bill b " +
                    "LEFT JOIN category c ON b.category_id = c.id " +
                    "WHERE b.user_id = ? AND b.type = ? ";

            List<Object> params = new ArrayList<>();
            params.add(userId);
            params.add(type);

            if (startDate != null) {
                sql += "AND b.bill_date >= ? ";
                params.add(new java.sql.Date(startDate.getTime()));
            }

            if (endDate != null) {
                sql += "AND b.bill_date <= ? ";
                params.add(new java.sql.Date(endDate.getTime()));
            }

            sql += "GROUP BY c.name ORDER BY total DESC";

            pstmt = conn.prepareStatement(sql);

            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            rs = pstmt.executeQuery();

            BigDecimal totalAmount = BigDecimal.ZERO;
            List<Map<String, Object>> tempList = new ArrayList<>();

            while (rs.next()) {
                Map<String, Object> stat = new HashMap<>();
                String categoryName = rs.getString("category_name");
                BigDecimal amount = rs.getBigDecimal("total");

                stat.put("category", categoryName);
                stat.put("amount", amount);
                tempList.add(stat);

                totalAmount = totalAmount.add(amount);
            }

            // 计算百分比
            for (Map<String, Object> stat : tempList) {
                BigDecimal amount = (BigDecimal) stat.get("amount");
                double percentage = 0;
                if (totalAmount.compareTo(BigDecimal.ZERO) != 0) {
                    percentage = amount.divide(totalAmount, 4, BigDecimal.ROUND_HALF_UP)
                            .multiply(new BigDecimal(100))
                            .doubleValue();
                }
                stat.put("percentage", percentage);
                stats.add(stat);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return stats;
    }

    @Override
    public List<Map<String, Object>> getTrendData(Integer userId, Date startDate, Date endDate) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Map<String, Object>> trendData = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT DATE_FORMAT(bill_date, '%Y-%m') as month, " +
                    "SUM(CASE WHEN type = 'INCOME' THEN amount ELSE 0 END) as income, " +
                    "SUM(CASE WHEN type = 'EXPENSE' THEN amount ELSE 0 END) as expense " +
                    "FROM bill " +
                    "WHERE user_id = ? ";

            List<Object> params = new ArrayList<>();
            params.add(userId);

            if (startDate != null) {
                sql += "AND bill_date >= ? ";
                params.add(new java.sql.Date(startDate.getTime()));
            }

            if (endDate != null) {
                sql += "AND bill_date <= ? ";
                params.add(new java.sql.Date(endDate.getTime()));
            }

            sql += "GROUP BY DATE_FORMAT(bill_date, '%Y-%m') " +
                    "ORDER BY month";

            pstmt = conn.prepareStatement(sql);

            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> data = new HashMap<>();
                data.put("month", rs.getString("month"));
                data.put("income", rs.getBigDecimal("income"));
                data.put("expense", rs.getBigDecimal("expense"));
                trendData.add(data);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return trendData;
    }
}