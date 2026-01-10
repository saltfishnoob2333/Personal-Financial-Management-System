package com.finance.util;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class DBUtil {
    private static String driver;
    private static String url;
    private static String username;
    private static String password;

    static {
        try {
            // 加载配置文件
            InputStream in = DBUtil.class.getClassLoader()
                    .getResourceAsStream("db.properties");
            Properties prop = new Properties();
            prop.load(in);

            driver = prop.getProperty("jdbc.driver");
            url = prop.getProperty("jdbc.url");
            username = prop.getProperty("jdbc.username");
            password = prop.getProperty("jdbc.password");

            // 加载驱动
            Class.forName(driver);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("数据库配置加载失败");
        }
    }

    // 获取数据库连接
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("获取数据库连接失败");
        }
    }

    // 关闭资源
    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void close(Connection conn, Statement stmt) {
        close(conn, stmt, null);
    }
}