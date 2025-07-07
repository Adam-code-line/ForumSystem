package main.forumsystem.src.dao.impl;

import main.forumsystem.src.dao.BaseDao;
import main.forumsystem.src.dao.UserDao;
import main.forumsystem.src.entity.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户数据访问实现类
 */
public class UserDaoImpl extends BaseDao implements UserDao {

    @Override
    public boolean addUser(User user) {
        String sql = """
            INSERT INTO users (username, password, email, nick_name, avatar, role, status, 
                             register_time, last_login, post_count, reputation) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        
        try {
            // 如果注册时间为空，设置为当前时间
            if (user.getRegisterTime() == null) {
                user.setRegisterTime(LocalDateTime.now());
            }
            
            int result = executeUpdate(sql,
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                user.getNickName(),
                user.getAvatar(),
                user.getRole().getValue(),
                user.getStatus().getValue(),
                Timestamp.valueOf(user.getRegisterTime()),
                user.getLastLogin() != null ? Timestamp.valueOf(user.getLastLogin()) : null,
                user.getPostCount(),
                user.getReputation()
            );
            
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try {
            int result = executeUpdate(sql, userId);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateUser(User user) {
        String sql = """
            UPDATE users SET username = ?, email = ?, nick_name = ?, avatar = ?, 
                           role = ?, status = ?, post_count = ?, reputation = ? 
            WHERE user_id = ?
            """;
        
        try {
            int result = executeUpdate(sql,
                user.getUsername(),
                user.getEmail(),
                user.getNickName(),
                user.getAvatar(),
                user.getRole().getValue(),
                user.getStatus().getValue(),
                user.getPostCount(),
                user.getReputation(),
                user.getUserId()
            );
            
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
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
        String sql = "SELECT * FROM users WHERE username = ? AND password = ? AND status = 'active'";
        return getSingleUser(sql, username, password);
    }

    @Override
    public boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) as count FROM users WHERE username = ?";
        try {
            ResultSet rs = executeQuery(sql, username);
            if (rs != null && rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) as count FROM users WHERE email = ?";
        try {
            ResultSet rs = executeQuery(sql, email);
            if (rs != null && rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
        try {
            int result = executeUpdate(sql, Timestamp.valueOf(LocalDateTime.now()), userId);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updatePostCount(int userId, int increment) {
        String sql = "UPDATE users SET post_count = post_count + ? WHERE user_id = ?";
        try {
            int result = executeUpdate(sql, increment, userId);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateReputation(int userId, int increment) {
        String sql = "UPDATE users SET reputation = reputation + ? WHERE user_id = ?";
        try {
            int result = executeUpdate(sql, increment, userId);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean changePassword(int userId, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE user_id = ?";
        try {
            int result = executeUpdate(sql, newPassword, userId);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean changeUserStatus(int userId, User.UserStatus status) {
        String sql = "UPDATE users SET status = ? WHERE user_id = ?";
        try {
            int result = executeUpdate(sql, status.getValue(), userId);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean changeUserRole(int userId, User.UserRole role) {
        String sql = "UPDATE users SET role = ? WHERE user_id = ?";
        try {
            int result = executeUpdate(sql, role.getValue(), userId);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
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
        try {
            ResultSet rs = executeQuery(sql);
            if (rs != null && rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int getActiveUserCount() {
        String sql = "SELECT COUNT(*) as count FROM users WHERE status = 'active'";
        try {
            ResultSet rs = executeQuery(sql);
            if (rs != null && rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int getTodayRegisterCount() {
        String sql = "SELECT COUNT(*) as count FROM users WHERE DATE(register_time) = CURDATE()";
        try {
            ResultSet rs = executeQuery(sql);
            if (rs != null && rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
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

        try {
            Object[] params = new Object[userIds.length];
            for (int i = 0; i < userIds.length; i++) {
                params[i] = userIds[i];
            }
            return executeUpdate(sql.toString(), params);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // 私有辅助方法：获取单个用户
    private User getSingleUser(String sql, Object... params) {
        try {
            ResultSet rs = executeQuery(sql, params);
            if (rs != null && rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 私有辅助方法：获取多个用户
    private List<User> getMultipleUsers(String sql, Object... params) {
        List<User> users = new ArrayList<>();
        try {
            ResultSet rs = executeQuery(sql, params);
            while (rs != null && rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
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
