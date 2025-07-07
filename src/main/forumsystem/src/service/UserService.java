package main.forumsystem.src.service;

import main.forumsystem.src.entity.User;
import java.util.List;

/**
 * 用户服务接口
 * 提供用户管理、用户信息查询、用户权限管理等服务
 */
public interface UserService {
    
    /**
     * 获取用户详细信息
     * @param userId 用户ID
     * @return 用户对象
     */
    User getUserById(int userId);
    
    /**
     * 根据用户名获取用户信息
     * @param username 用户名
     * @return 用户对象
     */
    User getUserByUsername(String username);
    
    /**
     * 根据邮箱获取用户信息
     * @param email 邮箱
     * @return 用户对象
     */
    User getUserByEmail(String email);
    
    /**
     * 更新用户个人信息
     * @param user 用户对象
     * @return 更新结果
     */
    UserResult updateUserProfile(User user);
    
    /**
     * 更新用户头像
     * @param userId 用户ID
     * @param avatarUrl 头像URL
     * @return 是否更新成功
     */
    boolean updateUserAvatar(int userId, String avatarUrl);
    
    /**
     * 更新用户昵称
     * @param userId 用户ID
     * @param nickName 新昵称
     * @return 更新结果
     */
    UserResult updateUserNickName(int userId, String nickName);
    
    /**
     * 获取用户列表（分页）
     * @param page 页码
     * @param size 每页大小
     * @return 用户列表
     */
    List<User> getUsersByPage(int page, int size);
    
    /**
     * 搜索用户
     * @param keyword 搜索关键词
     * @return 用户列表
     */
    List<User> searchUsers(String keyword);
    
    /**
     * 获取指定角色的用户列表
     * @param role 用户角色
     * @return 用户列表
     */
    List<User> getUsersByRole(User.UserRole role);
    
    /**
     * 获取指定状态的用户列表
     * @param status 用户状态
     * @return 用户列表
     */
    List<User> getUsersByStatus(User.UserStatus status);
    
    /**
     * 获取所有管理员
     * @return 管理员列表
     */
    List<User> getAllAdmins();
    
    /**
     * 获取所有版主
     * @return 版主列表
     */
    List<User> getAllModerators();
    
    /**
     * 增加用户帖子数量
     * @param userId 用户ID
     * @param increment 增加数量
     * @return 是否更新成功
     */
    boolean increasePostCount(int userId, int increment);
    
    /**
     * 减少用户帖子数量
     * @param userId 用户ID
     * @param decrement 减少数量
     * @return 是否更新成功
     */
    boolean decreasePostCount(int userId, int decrement);
    
    /**
     * 增加用户声誉值
     * @param userId 用户ID
     * @param increment 增加的声誉值
     * @return 是否更新成功
     */
    boolean increaseReputation(int userId, int increment);
    
    /**
     * 减少用户声誉值
     * @param userId 用户ID
     * @param decrement 减少的声誉值
     * @return 是否更新成功
     */
    boolean decreaseReputation(int userId, int decrement);
    
    /**
     * 提升用户为版主
     * @param userId 用户ID
     * @return 操作结果
     */
    UserResult promoteToModerator(int userId);
    
    /**
     * 降级版主为普通用户
     * @param userId 用户ID
     * @return 操作结果
     */
    UserResult demoteToUser(int userId);
    
    /**
     * 提升用户为管理员
     * @param userId 用户ID
     * @return 操作结果
     */
    UserResult promoteToAdmin(int userId);
    
    /**
     * 封禁用户
     * @param userId 用户ID
     * @return 操作结果
     */
    UserResult banUser(int userId);
    
    /**
     * 解封用户
     * @param userId 用户ID
     * @return 操作结果
     */
    UserResult unbanUser(int userId);
    
    /**
     * 激活用户
     * @param userId 用户ID
     * @return 操作结果
     */
    UserResult activateUser(int userId);
    
    /**
     * 检查用户是否存在
     * @param userId 用户ID
     * @return 是否存在
     */
    boolean userExists(int userId);
    
    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return 是否存在
     */
    boolean usernameExists(String username);
    
    /**
     * 检查邮箱是否存在
     * @param email 邮箱
     * @return 是否存在
     */
    boolean emailExists(String email);
    
    /**
     * 获取用户统计信息
     * @return 用户统计对象
     */
    UserStatistics getUserStatistics();
    
    /**
     * 删除用户
     * @param userId 用户ID
     * @return 操作结果
     */
    UserResult deleteUser(int userId);
    
    /**
     * 批量删除用户
     * @param userIds 用户ID数组
     * @return 删除成功的用户数量
     */
    int batchDeleteUsers(int[] userIds);
    
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
        
        public UserStatistics() {}
        
        public UserStatistics(int totalUsers, int activeUsers, int bannedUsers, 
                            int todayRegisterCount, int adminCount, int moderatorCount) {
            this.totalUsers = totalUsers;
            this.activeUsers = activeUsers;
            this.bannedUsers = bannedUsers;
            this.todayRegisterCount = todayRegisterCount;
            this.adminCount = adminCount;
            this.moderatorCount = moderatorCount;
        }
        
        // Getters and Setters
        public int getTotalUsers() { return totalUsers; }
        public void setTotalUsers(int totalUsers) { this.totalUsers = totalUsers; }
        
        public int getActiveUsers() { return activeUsers; }
        public void setActiveUsers(int activeUsers) { this.activeUsers = activeUsers; }
        
        public int getBannedUsers() { return bannedUsers; }
        public void setBannedUsers(int bannedUsers) { this.bannedUsers = bannedUsers; }
        
        public int getTodayRegisterCount() { return todayRegisterCount; }
        public void setTodayRegisterCount(int todayRegisterCount) { this.todayRegisterCount = todayRegisterCount; }
        
        public int getAdminCount() { return adminCount; }
        public void setAdminCount(int adminCount) { this.adminCount = adminCount; }
        
        public int getModeratorCount() { return moderatorCount; }
        public void setModeratorCount(int moderatorCount) { this.moderatorCount = moderatorCount; }
    }
}
