package main.forumsystem.src.util;

import main.forumsystem.src.dao.BaseDao;
import java.sql.*;
import java.util.*;

public class DatabaseUtil {
    
    /**
     * 执行查询并返回单个对象的映射
     * @param sql SQL语句
     * @param params 参数
     * @return Map<String, Object> 单行数据映射
     */
    public static Map<String, Object> queryForMap(String sql, Object... params) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = BaseDao.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            // 设置参数
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return resultSetToMap(rs);
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            BaseDao.close(conn, pstmt, rs);
        }
    }
    
    /**
     * 执行查询并返回对象列表
     * @param sql SQL语句
     * @param params 参数
     * @return List<Map<String, Object>> 查询结果列表
     */
    public static List<Map<String, Object>> queryForList(String sql, Object... params) {
        List<Map<String, Object>> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = BaseDao.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            // 设置参数
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(resultSetToMap(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDao.close(conn, pstmt, rs);
        }
        return list;
    }
    
    /**
     * 查询单个值
     * @param sql SQL语句
     * @param params 参数
     * @return Object 单个值
     */
    public static Object queryForObject(String sql, Object... params) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = BaseDao.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            // 设置参数
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getObject(1);
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            BaseDao.close(conn, pstmt, rs);
        }
    }
    
    /**
     * 查询记录数量
     * @param tableName 表名
     * @param whereClause WHERE条件
     * @param params 参数
     * @return long 记录数量
     */
    public static long count(String tableName, String whereClause, Object... params) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM " + tableName);
        if (whereClause != null && !whereClause.trim().isEmpty()) {
            sql.append(" WHERE ").append(whereClause);
        }
        
        Object result = queryForObject(sql.toString(), params);
        return result != null ? ((Number) result).longValue() : 0;
    }
    
    /**
     * 检查记录是否存在
     * @param tableName 表名
     * @param whereClause WHERE条件
     * @param params 参数
     * @return boolean 是否存在
     */
    public static boolean exists(String tableName, String whereClause, Object... params) {
        return count(tableName, whereClause, params) > 0;
    }
    
    /**
     * 分页查询
     * @param sql 基础SQL语句
     * @param page 页码(从1开始)
     * @param pageSize 每页大小
     * @param params 参数
     * @return Map<String, Object> 包含数据和分页信息
     */
    public static Map<String, Object> queryForPage(String sql, int page, int pageSize, Object... params) {
        Map<String, Object> result = new HashMap<>();
        
        // 计算总记录数
        String countSql = "SELECT COUNT(*) FROM (" + sql + ") AS temp_table";
        long totalCount = ((Number) queryForObject(countSql, params)).longValue();
        
        // 计算分页信息
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);
        int offset = (page - 1) * pageSize;
        
        // 分页查询
        String pageSql = sql + " LIMIT ? OFFSET ?";
        Object[] pageParams = new Object[params.length + 2];
        System.arraycopy(params, 0, pageParams, 0, params.length);
        pageParams[params.length] = pageSize;
        pageParams[params.length + 1] = offset;
        
        List<Map<String, Object>> data = queryForList(pageSql, pageParams);
        
        result.put("data", data);
        result.put("totalCount", totalCount);
        result.put("totalPages", totalPages);
        result.put("currentPage", page);
        result.put("pageSize", pageSize);
        result.put("hasNextPage", page < totalPages);
        result.put("hasPreviousPage", page > 1);
        
        return result;
    }
    
    /**
     * 批量插入数据
     * @param tableName 表名
     * @param columns 列名数组
     * @param valuesList 值列表，每个元素是一行数据的值数组
     * @return int[] 每条插入语句影响的行数
     */
    public static int[] batchInsert(String tableName, String[] columns, List<Object[]> valuesList) {
        StringBuilder sql = new StringBuilder("INSERT INTO " + tableName + " (");
        for (int i = 0; i < columns.length; i++) {
            sql.append(columns[i]);
            if (i < columns.length - 1) {
                sql.append(", ");
            }
        }
        sql.append(") VALUES (");
        for (int i = 0; i < columns.length; i++) {
            sql.append("?");
            if (i < columns.length - 1) {
                sql.append(", ");
            }
        }
        sql.append(")");
        
        return batchExecute(sql.toString(), valuesList);
    }
    
    /**
     * 批量执行SQL
     * @param sql SQL语句
     * @param paramsList 参数列表
     * @return int[] 每条语句影响的行数
     */
    public static int[] batchExecute(String sql, List<Object[]> paramsList) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = BaseDao.getConnection();
            conn.setAutoCommit(false); // 开始事务
            
            pstmt = conn.prepareStatement(sql);
            
            for (Object[] params : paramsList) {
                for (int i = 0; i < params.length; i++) {
                    pstmt.setObject(i + 1, params[i]);
                }
                pstmt.addBatch();
            }
            
            int[] result = pstmt.executeBatch();
            conn.commit(); // 提交事务
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback(); // 回滚事务
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return new int[0];
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // 恢复自动提交
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            BaseDao.close(conn, pstmt, null);
        }
    }
    
    /**
     * 执行事务
     * @param operations 事务操作列表
     * @return boolean 是否执行成功
     */
    public static boolean executeTransaction(List<TransactionOperation> operations) {
        Connection conn = null;
        try {
            conn = BaseDao.getConnection();
            conn.setAutoCommit(false); // 开始事务
            
            for (TransactionOperation operation : operations) {
                operation.execute(conn);
            }
            
            conn.commit(); // 提交事务
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback(); // 回滚事务
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // 恢复自动提交
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            BaseDao.close(conn, null, null);
        }
    }
    
    /**
     * 将ResultSet转换为Map
     * @param rs ResultSet对象
     * @return Map<String, Object> 数据映射
     */
    private static Map<String, Object> resultSetToMap(ResultSet rs) throws SQLException {
        Map<String, Object> map = new HashMap<>();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        
        for (int i = 1; i <= columnCount; i++) {
            String columnName = metaData.getColumnLabel(i);
            Object value = rs.getObject(i);
            map.put(columnName, value);
        }
        
        return map;
    }
    
    /**
     * 获取表的所有列名
     * @param tableName 表名
     * @return List<String> 列名列表
     */
    public static List<String> getTableColumns(String tableName) {
        List<String> columns = new ArrayList<>();
        Connection conn = null;
        try {
            conn = BaseDao.getConnection();
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet rs = metaData.getColumns(null, null, tableName, null);
            
            while (rs.next()) {
                columns.add(rs.getString("COLUMN_NAME"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDao.close(conn, null, null);
        }
        return columns;
    }
    
    /**
     * 事务操作接口
     */
    @FunctionalInterface
    public interface TransactionOperation {
        void execute(Connection conn) throws SQLException;
    }
    
    /**
     * 创建简单的事务操作
     * @param sql SQL语句
     * @param params 参数
     * @return TransactionOperation 事务操作
     */
    public static TransactionOperation createOperation(String sql, Object... params) {
        return (conn) -> {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (int i = 0; i < params.length; i++) {
                    pstmt.setObject(i + 1, params[i]);
                }
                pstmt.executeUpdate();
            }
        };
    }
}
