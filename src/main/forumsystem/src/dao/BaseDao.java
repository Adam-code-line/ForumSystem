package main.forumsystem.src.dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class BaseDao {
    private static String DRIVER;
    private static String URL;
    private static String USERNAME;
    private static String PASSWORD;

    static {
        // 初始化数据库连接
        init();
    }

    /*
     * 数据库初始化
     */
    public static void init() {
        try {
            // 加载配置文件
            Properties properties = new Properties();
            String config = "database.properties";
            InputStream resourceAsStream = BaseDao.class.getClassLoader().getResourceAsStream(config);
            if (resourceAsStream == null) {
                throw new RuntimeException("配置文件未找到！");
            }
            properties.load(resourceAsStream);

            DRIVER = properties.getProperty("driver");
            URL = properties.getProperty("url");
            USERNAME = properties.getProperty("username");
            PASSWORD = properties.getProperty("password");

            // 加载数据库驱动
            Class.forName(DRIVER);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("配置文件加载失败或驱动加载失败！");
        }
    }

    /**
     * 获取数据库连接
     * @return Connection 对象
     */
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("数据库连接失败！");
        }
    }

    /**
     * 关闭资源
     * @param conn 数据库连接
     * @param pstmt 预编译语句
     * @param rs 结果集
     */
    public static void close(Connection conn, PreparedStatement pstmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试数据库连接
     */
    public static void testConnection() {
        Connection conn = null;
        try {
            conn = getConnection();
            System.out.println("数据库连接成功！");
        } catch (RuntimeException e) {
            System.out.println("数据库连接失败！");
        } finally {
            close(conn, null, null);
        }
    }

    /**
     * 通用的增删改操作
     * @param sql SQL语句
     * @param params 参数列表
     * @return 影响的行数
     */
    public static int executeUpdate(String sql, Object... params) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);

            // 设置参数
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            close(conn, pstmt, null);
        }
    }

    /**
     * 查询操作
     * @param sql SQL语句
     * @param params 参数列表
     * @return ResultSet 结果集
     */
    public static ResultSet executeQuery(String sql, Object... params) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);

            // 设置参数
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }

            rs = pstmt.executeQuery();
            return rs;
        } catch (SQLException e) {
            e.printStackTrace();
            close(conn, pstmt, rs);
            return null;
        }
    }

    /**
     * 插入数据
     * @param tableName 表名
     * @param columns 列名数组
     * @param values 值数组
     * @return 影响的行数
     */
    public static int insert(String tableName, String[] columns, Object[] values) {
        if (columns.length != values.length) {
            throw new IllegalArgumentException("列名和值的数量不匹配！");
        }

        StringBuilder sql = new StringBuilder("INSERT INTO " + tableName + " (");
        for (int i = 0; i < columns.length; i++) {
            sql.append(columns[i]);
            if (i < columns.length - 1) {
                sql.append(", ");
            }
        }
        sql.append(") VALUES (");
        for (int i = 0; i < values.length; i++) {
            sql.append("?");
            if (i < values.length - 1) {
                sql.append(", ");
            }
        }
        sql.append(")");

        return executeUpdate(sql.toString(), values);
    }

    /**
     * 更新数据
     * @param tableName 表名
     * @param columns 要更新的列名数组
     * @param values 要更新的值数组
     * @param whereClause WHERE条件
     * @param whereParams WHERE条件参数
     * @return 影响的行数
     */
    public static int update(String tableName, String[] columns, Object[] values, String whereClause, Object... whereParams) {
        if (columns.length != values.length) {
            throw new IllegalArgumentException("列名和值的数量不匹配！");
        }

        StringBuilder sql = new StringBuilder("UPDATE " + tableName + " SET ");
        for (int i = 0; i < columns.length; i++) {
            sql.append(columns[i]).append(" = ?");
            if (i < columns.length - 1) {
                sql.append(", ");
            }
        }

        if (whereClause != null && !whereClause.trim().isEmpty()) {
            sql.append(" WHERE ").append(whereClause);
        }

        // 合并参数数组
        Object[] allParams = new Object[values.length + whereParams.length];
        System.arraycopy(values, 0, allParams, 0, values.length);
        System.arraycopy(whereParams, 0, allParams, values.length, whereParams.length);

        return executeUpdate(sql.toString(), allParams);
    }

    /**
     * 删除数据
     * @param tableName 表名
     * @param whereClause WHERE条件
     * @param whereParams WHERE条件参数
     * @return 影响的行数
     */
    public static int delete(String tableName, String whereClause, Object... whereParams) {
        StringBuilder sql = new StringBuilder("DELETE FROM " + tableName);

        if (whereClause != null && !whereClause.trim().isEmpty()) {
            sql.append(" WHERE ").append(whereClause);
        }

        return executeUpdate(sql.toString(), whereParams);
    }


}