package main.forumsystem.src.controller;

import main.forumsystem.src.service.LoginService;
import main.forumsystem.src.service.impl.LoginServiceImpl;
import main.forumsystem.src.entity.User;

/**
 * 认证控制器
 * 专门处理用户登录、注册、登出等认证相关功能
 */
public class AuthController {
    
    private final LoginService loginService;
    
    public AuthController() {
        this.loginService = new LoginServiceImpl();
    }
    
    /**
     * 用户登录
     */
    public LoginService.LoginResult login(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            return new LoginService.LoginResult(false, "用户名不能为空", null);
        }
        
        if (password == null || password.trim().isEmpty()) {
            return new LoginService.LoginResult(false, "密码不能为空", null);
        }
        
        return loginService.login(username.trim(), password);
    }
    
    /**
     * 用户注册
     */
    public LoginService.LoginResult register(String username, String password, String email) {
        if (username == null || username.trim().isEmpty()) {
            return new LoginService.LoginResult(false, "用户名不能为空", null);
        }
        
        if (password == null || password.trim().isEmpty()) {
            return new LoginService.LoginResult(false, "密码不能为空", null);
        }
        
        if (email == null || email.trim().isEmpty()) {
            return new LoginService.LoginResult(false, "邮箱不能为空", null);
        }
        
        return loginService.register(username.trim(), password, email.trim());
    }
    
    /**
     * 管理员注册
     */
    public LoginService.LoginResult registerAdmin(String username, String password, String email, String adminKey) {
        if (username == null || username.trim().isEmpty()) {
            return new LoginService.LoginResult(false, "用户名不能为空", null);
        }
        
        if (password == null || password.trim().isEmpty()) {
            return new LoginService.LoginResult(false, "密码不能为空", null);
        }
        
        if (email == null || email.trim().isEmpty()) {
            return new LoginService.LoginResult(false, "邮箱不能为空", null);
        }
        
        if (adminKey == null || adminKey.trim().isEmpty()) {
            return new LoginService.LoginResult(false, "管理员密钥不能为空", null);
        }
        
        return loginService.registerAdmin(username.trim(), password, email.trim(), adminKey);
    }
    
    /**
     * 用户登出
     */
    public boolean logout(int userId) {
        if (userId <= 0) {
            return false;
        }
        
        return loginService.logout(userId);
    }
    
    /**
     * 修改密码
     */
    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        if (userId <= 0) {
            return false;
        }
        
        if (oldPassword == null || oldPassword.trim().isEmpty()) {
            return false;
        }
        
        if (newPassword == null || newPassword.trim().isEmpty()) {
            return false;
        }
        
        if (oldPassword.equals(newPassword)) {
            return false; // 新密码不能与旧密码相同
        }
        
        return loginService.changePassword(userId, oldPassword, newPassword);
    }
    
    /**
     * 验证用户权限
     */
    public boolean hasPermission(int userId, User.UserRole requiredRole) {
        if (userId <= 0 || requiredRole == null) {
            return false;
        }
        
        return loginService.hasPermission(userId, requiredRole);
    }
    
    /**
     * 检查用户是否被封禁
     */
    public boolean isUserBanned(int userId) {
        if (userId <= 0) {
            return true;
        }
        
        return loginService.isUserBanned(userId);
    }
}
