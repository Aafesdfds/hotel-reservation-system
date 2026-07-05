package com.hotel.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 数据库连接工具。连接信息优先读环境变量，没有再读 db.properties，方便区分开发和生产环境。
 */
public class DBUtil {

    private static String url;
    private static String user;
    private static String password;

    static {
        Properties p = new Properties();
        try (InputStream in = DBUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                p.load(in);
            }
        } catch (Exception e) {
            System.err.println("读取 db.properties 失败，使用默认配置：" + e.getMessage());
        }

        String host = env("DB_HOST", p.getProperty("db.host", "localhost"));
        String port = env("DB_PORT", p.getProperty("db.port", "3306"));
        String name = env("DB_NAME", p.getProperty("db.name", "hotel"));
        user = env("DB_USER", p.getProperty("db.user", "root"));
        password = env("DB_PASSWORD", p.getProperty("db.password", ""));
        url = "jdbc:mysql://" + host + ":" + port + "/" + name
                + "?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8&allowPublicKeyRetrieval=true";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("找不到 MySQL 驱动", e);
        }
    }

    private static String env(String key, String def) {
        String v = System.getenv(key);
        return (v == null || v.trim().isEmpty()) ? def : v.trim();
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    /** 静默关闭连接、语句、结果集 */
    public static void close(AutoCloseable... resources) {
        for (AutoCloseable r : resources) {
            if (r != null) {
                try {
                    r.close();
                } catch (Exception ignored) {
                }
            }
        }
    }
}
