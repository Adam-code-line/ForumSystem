package main.forumsystem.src.service;

import main.forumsystem.src.entity.User;
import java.util.List;

/**
 * 用户服务接口
 * 专注于用户管理的核心业务逻辑，不包含权限验证
 */
public interface UserService {
    
    // ==================== 基础查询功能 ====================

    /*
     * 获取用户名称
     */
    String getUserName(int userId);
    
    /**
     * 获取用户详细信息
     */
    User getUserById(int userId);
    
    /**
     * 根据用户名获取用户信息
     */
    User getUserByUsername(String username);
    
    /**
     * 根据邮箱获取用户信息
     */
    User getUserByEmail(String email);
    
    // ==================== 用户信息管理 ====================
    
    /**
     * 更新用户个人信息
     */
    UserResult updateUserProfile(User user);
    
    /**
     * 更新用户头像
     */
    boolean updateUserAvatar(int userId, String avatarUrl);
    
    /**
     * 更新用户昵称
     */
    UserResult updateUserNickName(int userId, String nickName);
    
    // ==================== 用户查询功能 ====================
    
    /**
     * 分页获取用户列表
     */
    List<User> getUsersByPage(int page, int size);
    
    /**
     * 搜索用户
     */
    List<User> searchUsers(String keyword);
    
    /**
     * 获取指定角色的用户
     */
    List<User> getUsersByRole(User.UserRole role);
    
    /**
     * 获取指定状态的用户
     */
    List<User> getUsersByStatus(User.UserStatus status);
    
    /**
     * 获取所有管理员
     */
    List<User> getAllAdmins();
    
    /**
     * 获取所有版主
     */
    List<User> getAllModerators();
    
    // ==================== 用户角色管理 ====================
    
    /**
     * 提升用户为版主（纯业务逻辑，不验证权限）
     */
    UserResult promoteToModerator(int userId);
    
    /**
     * 降级版主为普通用户（纯业务逻辑，不验证权限）
     */
    UserResult demoteToUser(int userId);
    
    /**
     * 提升用户为管理员（纯业务逻辑，不验证权限）
     */
    UserResult promoteToAdmin(int userId);
    
    // ==================== 用户状态管理 ====================
    
    /**
     * 封禁用户（纯业务逻辑，不验证权限）
     */
    UserResult banUser(int userId);
    
    /**
     * 解封用户（纯业务逻辑，不验证权限）
     */
    UserResult unbanUser(int userId);
    
    /**
     * 激活用户（纯业务逻辑，不验证权限）
     */
    UserResult activateUser(int userId);
    
    // ==================== 用户删除管理 ====================
    
    /**
     * 删除用户（纯业务逻辑，不验证权限）
     */
    UserResult deleteUser(int userId);
    
    /**
     * 批量删除用户（纯业务逻辑，不验证权限）
     */
    int batchDeleteUsers(int[] userIds);
    
    // ==================== 用户统计和计数 ====================
    
    /**
     * 增加用户发帖数量
     */
    boolean increasePostCount(int userId, int increment);
    
    /**
     * 减少用户发帖数量
     */
    boolean decreasePostCount(int userId, int decrement);
    
    /**
     * 增加用户声誉值
     */
    boolean increaseReputation(int userId, int increment);
    
    /**
     * 减少用户声誉值
     */
    boolean decreaseReputation(int userId, int decrement);
    
    // ==================== 用户存在性检查 ====================
    
    /**
     * 检查用户是否存在
     */
    boolean userExists(int userId);
    
    /**
     * 检查用户名是否存在
     */
    boolean usernameExists(String username);
    
    /**
     * 检查邮箱是否存在
     */
    boolean emailExists(String email);
    
    // ==================== 统计信息 ====================
    
    /**
     * 获取用户统计信息
     */
    UserStatistics getUserStatistics();
    
    // ==================== 结果类 ====================
    
    /**
     * 用户操作结果类
     */
    class UserResult {
        private boolean success;
        private String message;
        private User user;
        
        public UserResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public UserResult(boolean success, String message, User user) {
            this.success = success;
            this.message = message;
            this.user = user;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public User getUser() {
            return user;
        }
    }
    
    /**
     * 用户统计信息类
     */
    class UserStatistics {
        private int totalUsers;
        private int activeUsers;
        private int bannedUsers;
        private int todayRegisterCount;
        private int adminCount;
        private int moderatorCount;
        
        public UserStatistics(int totalUsers, int activeUsers, int bannedUsers, 
                            int todayRegisterCount, int adminCount, int moderatorCount) {
            this.totalUsers = totalUsers;
            this.activeUsers = activeUsers;
            this.bannedUsers = bannedUsers;
            this.todayRegisterCount = todayRegisterCount;
            this.adminCount = adminCount;
            this.moderatorCount = moderatorCount;
        }
        
        // Getters
        public int getTotalUsers() { return totalUsers; }
        public int getActiveUsers() { return activeUsers; }
        public int getBannedUsers() { return bannedUsers; }
        public int getTodayRegisterCount() { return todayRegisterCount; }
        public int getAdminCount() { return adminCount; }
        public int getModeratorCount() { return moderatorCount; }
    }
}
