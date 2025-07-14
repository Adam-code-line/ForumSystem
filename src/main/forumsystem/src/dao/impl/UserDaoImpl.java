package main.forumsystem.src.dao.impl;

import main.forumsystem.src.dao.BaseDao;
import main.forumsystem.src.dao.UserDao;
import main.forumsystem.src.entity.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户数据访问实现类
 */
public class UserDaoImpl implements UserDao {

    /**
     * 添加用户到数据库。
     * 如果用户的注册时间为空，则设置为当前时间。
     * 
     * @param user 用户对象
     * @return 添加成功返回true，否则返回false
     */
    @Override
    public boolean addUser(User user) {
        String sql = """
            INSERT INTO users (username, password, email, nick_name, avatar, role, status, 
                             register_time, last_login, post_count, reputation) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            // 如果注册时间为空，设置为当前时间
            if (user.getRegisterTime() == null) {
                user.setRegisterTime(LocalDateTime.now());
            }
            
            conn = BaseDao.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            // 设置SQL参数
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getNickName());
            pstmt.setString(5, user.getAvatar());
            pstmt.setString(6, user.getRole().getValue());
            pstmt.setString(7, user.getStatus().getValue());
            pstmt.setTimestamp(8, Timestamp.valueOf(user.getRegisterTime()));
            pstmt.setTimestamp(9, user.getLastLogin() != null ? Timestamp.valueOf(user.getLastLogin()) : null);
            pstmt.setInt(10, user.getPostCount());
            pstmt.setInt(11, user.getReputation());
            
            // 执行插入操作
            int result = pstmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            // 关闭资源
            BaseDao.close(conn, pstmt, null);
        }
    }

    /**
     * 根据用户ID删除用户。
     * 
     * @param userId 用户ID
     * @return 删除成功返回true，否则返回false
     */
    @Override
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = BaseDao.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            BaseDao.close(conn, pstmt, null);
        }
    }

    @Override
    public boolean updateUser(User user) {
        String sql = """
            UPDATE users SET username = ?, email = ?, nick_name = ?, avatar = ?, 
                           role = ?, status = ?, post_count = ?, reputation = ? 
            WHERE user_id = ?
            """;
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = BaseDao.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getNickName());
            pstmt.setString(4, user.getAvatar());
            pstmt.setString(5, user.getRole().getValue());
            pstmt.setString(6, user.getStatus().getValue());
            pstmt.setInt(7, user.getPostCount());
            pstmt.setInt(8, user.getReputation());
            pstmt.setInt(9, user.getUserId());
            
            int result = pstmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            BaseDao.close(conn, pstmt, null);
        }
    }

    @Override
    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        return getSingleUser(sql, userId);
    }

    @Override
    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        return getSingleUser(sql, username);
    }

    @Override
    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        return getSingleUser(sql, email);
    }

    @Override
    public User validateLogin(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        return getSingleUser(sql, username, password);
    }

    @Override
    public boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) as count FROM users WHERE username = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = BaseDao.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDao.close(conn, pstmt, rs);
        }
        return false;
    }

    @Override
    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) as count FROM users WHERE email = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = BaseDao.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDao.close(conn, pstmt, rs);
        }
        return false;
    }

    @Override
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users ORDER BY register_time DESC";
        return getMultipleUsers(sql);
    }

    @Override
    public List<User> getUsersByPage(int page, int size) {
        String sql = "SELECT * FROM users ORDER BY register_time DESC LIMIT ? OFFSET ?";
        int offset = (page - 1) * size;
        return getMultipleUsers(sql, size, offset);
    }

    @Override
    public List<User> getUsersByRole(User.UserRole role) {
        String sql = "SELECT * FROM users WHERE role = ? ORDER BY register_time DESC";
        return getMultipleUsers(sql, role.getValue());
    }

    @Override
    public List<User> getUsersByStatus(User.UserStatus status) {
        String sql = "SELECT * FROM users WHERE status = ? ORDER BY register_time DESC";
        return getMultipleUsers(sql, status.getValue());
    }

    @Override
    public boolean updateLastLogin(int userId) {
        String sql = "UPDATE users SET last_login = ? WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = BaseDao.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(2, userId);
            
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            BaseDao.close(conn, pstmt, null);
        }
    }

    @Override
    public boolean updatePostCount(int userId, int increment) {
        String sql = "UPDATE users SET post_count = post_count + ? WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = BaseDao.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, increment);
            pstmt.setInt(2, userId);
            
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            BaseDao.close(conn, pstmt, null);
        }
    }

    @Override
    public boolean updateReputation(int userId, int increment) {
        String sql = "UPDATE users SET reputation = reputation + ? WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = BaseDao.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, increment);
            pstmt.setInt(2, userId);
            
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            BaseDao.close(conn, pstmt, null);
        }
    }

    @Override
    public boolean changePassword(int userId, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = BaseDao.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newPassword);
            pstmt.setInt(2, userId);
            
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            BaseDao.close(conn, pstmt, null);
        }
    }

    @Override
    public boolean changeUserStatus(int userId, User.UserStatus status) {
        String sql = "UPDATE users SET status = ? WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = BaseDao.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status.getValue());
            pstmt.setInt(2, userId);
            
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            BaseDao.close(conn, pstmt, null);
        }
    }

    @Override
    public boolean changeUserRole(int userId, User.UserRole role) {
        String sql = "UPDATE users SET role = ? WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = BaseDao.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, role.getValue());
            pstmt.setInt(2, userId);
            
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            BaseDao.close(conn, pstmt, null);
        }
    }

    @Override
    public List<User> searchUsers(String keyword) {
        String sql = "SELECT * FROM users WHERE username LIKE ? OR nick_name LIKE ? ORDER BY register_time DESC";
        String searchPattern = "%" + keyword + "%";
        return getMultipleUsers(sql, searchPattern, searchPattern);
    }

    @Override
    public int getUserCount() {
        String sql = "SELECT COUNT(*) as count FROM users";
        return getCount(sql);
    }

    @Override
    public int getActiveUserCount() {
        String sql = "SELECT COUNT(*) as count FROM users WHERE status = 'active'";
        return getCount(sql);
    }

    @Override
    public int getTodayRegisterCount() {
        String sql = "SELECT COUNT(*) as count FROM users WHERE DATE(register_time) = CURDATE()";
        return getCount(sql);
    }

    @Override
    public int batchDeleteUsers(int[] userIds) {
        if (userIds == null || userIds.length == 0) {
            return 0;
        }

        StringBuilder sql = new StringBuilder("DELETE FROM users WHERE user_id IN (");
        for (int i = 0; i < userIds.length; i++) {
            sql.append("?");
            if (i < userIds.length - 1) {
                sql.append(",");
            }
        }
        sql.append(")");

        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = BaseDao.getConnection();
            pstmt = conn.prepareStatement(sql.toString());
            
            for (int i = 0; i < userIds.length; i++) {
                pstmt.setInt(i + 1, userIds[i]);
            }
            
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            BaseDao.close(conn, pstmt, null);
        }
    }

    // 私有辅助方法：获取单个用户
    private User getSingleUser(String sql, Object... params) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = BaseDao.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDao.close(conn, pstmt, rs);
        }
        return null;
    }

    // 私有辅助方法：获取多个用户
    private List<User> getMultipleUsers(String sql, Object... params) {
        List<User> users = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = BaseDao.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            
            rs = pstmt.executeQuery();
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDao.close(conn, pstmt, rs);
        }
        return users;
    }

    // 私有辅助方法：获取数量
    private int getCount(String sql, Object... params) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = BaseDao.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDao.close(conn, pstmt, rs);
        }
        return 0;
    }

    // 私有辅助方法：将ResultSet映射为User对象
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setEmail(rs.getString("email"));
        user.setNickName(rs.getString("nick_name"));
        user.setAvatar(rs.getString("avatar"));
        user.setRole(User.UserRole.fromValue(rs.getString("role")));
        user.setStatus(User.UserStatus.fromValue(rs.getString("status")));
        user.setPostCount(rs.getInt("post_count"));
        user.setReputation(rs.getInt("reputation"));

        // 处理时间字段
        Timestamp registerTime = rs.getTimestamp("register_time");
        if (registerTime != null) {
            user.setRegisterTime(registerTime.toLocalDateTime());
        }

        Timestamp lastLogin = rs.getTimestamp("last_login");
        if (lastLogin != null) {
            user.setLastLogin(lastLogin.toLocalDateTime());
        }

        return user;
    }
}
