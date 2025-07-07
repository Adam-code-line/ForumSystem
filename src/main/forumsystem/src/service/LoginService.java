package main.forumsystem.src.service;

import main.forumsystem.src.entity.User;

/**
 * 登录服务接口
 * 提供用户登录、注册、权限验证等服务
 */
public interface LoginService {
    
    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录结果对象
     */
    LoginResult login(String username, String password);
    
    /**
     * 用户注册
     * @param username 用户名
     * @param password 密码
     * @param email 邮箱
     * @return 注册结果对象
     */
    RegisterResult register(String username, String password, String email);
    
    /**
     * 管理员注册
     * @param username 用户名
     * @param password 密码
     * @param email 邮箱
     * @param adminKey 管理员密钥
     * @return 注册结果对象
     */
    RegisterResult registerAdmin(String username, String password, String email, String adminKey);
    
    /**
     * 用户登出
     * @param userId 用户ID
     * @return 是否登出成功
     */
    boolean logout(int userId);
    
    /**
     * 验证用户权限
     * @param userId 用户ID
     * @param requiredRole 需要的角色
     * @return 是否有权限
     */
    boolean hasPermission(int userId, User.UserRole requiredRole);
    
    /**
     * 检查用户是否被封禁
     * @param userId 用户ID
     * @return 是否被封禁
     */
    boolean isUserBanned(int userId);
    
    /**
     * 修改密码
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 修改结果
     */
    boolean changePassword(int userId, String oldPassword, String newPassword);
    
    /**
     * 登录结果类
     */
    class LoginResult {
        private boolean success;
        private String message;
        private User user;
        
        public LoginResult(boolean success, String message, User user) {
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
     * 注册结果类
     */
    class RegisterResult {
        private boolean success;
        private String message;
        private int userId;
        
        public RegisterResult(boolean success, String message, int userId) {
            this.success = success;
            this.message = message;
            this.userId = userId;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public int getUserId() {
            return userId;
        }
    }
}
