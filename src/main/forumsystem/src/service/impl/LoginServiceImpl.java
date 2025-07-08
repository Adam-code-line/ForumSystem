package main.forumsystem.src.service.impl;

import main.forumsystem.src.dao.UserDao;
import main.forumsystem.src.dao.impl.UserDaoImpl;
import main.forumsystem.src.entity.User;
import main.forumsystem.src.service.LoginService;
import main.forumsystem.src.factory.UserFactory;
import main.forumsystem.src.factory.impl.UserFactoryImpl;
import main.forumsystem.src.util.PasswordUtil;
import main.forumsystem.src.util.ValidationUtil;

import java.time.LocalDateTime;

/**
 * 登录服务实现类
 * 专注于登录、注册、权限验证等核心业务逻辑
 */
public class LoginServiceImpl implements LoginService {
    
    private final UserDao userDao;
    private final UserFactory userFactory;
    private static final String ADMIN_KEY = "FORUM_ADMIN_2024"; // 管理员注册密钥
    
    public LoginServiceImpl() {
        this.userDao = new UserDaoImpl();
        this.userFactory = new UserFactoryImpl();
    }
    
    @Override
    public LoginResult login(String username, String password) {
        if (ValidationUtil.isEmpty(username) || ValidationUtil.isEmpty(password)) {
            return new LoginResult(false, "用户名和密码不能为空", null);
        }
        
        try {
            // 获取用户信息
            User user = userDao.getUserByUsername(username.trim());
            if (user == null) {
                return new LoginResult(false, "用户名不存在", null);
            }
            
            // 验证密码（使用加密工具）
            if (!PasswordUtil.verify(password, user.getPassword())) {
                return new LoginResult(false, "密码错误", null);
            }
            
            // 检查用户状态
            if (user.getStatus() == User.UserStatus.BANNED) {
                return new LoginResult(false, "账户已被封禁", null);
            }
            
            if (user.getStatus() == User.UserStatus.INACTIVE) {
                return new LoginResult(false, "账户未激活", null);
            }
            
            // 更新最后登录时间
            userDao.updateLastLogin(user.getUserId());
            
            return new LoginResult(true, "登录成功", user);
            
        } catch (Exception e) {
            e.printStackTrace();
            return new LoginResult(false, "系统错误，登录失败", null);
        }
    }
    
    @Override
    public LoginResult register(String username, String password, String email) {
        if (ValidationUtil.isEmpty(username) || ValidationUtil.isEmpty(password) || ValidationUtil.isEmpty(email)) {
            return new LoginResult(false, "用户信息不能为空", null);
        }
        
        if (!ValidationUtil.isValidEmail(email)) {
            return new LoginResult(false, "邮箱格式不正确", null);
        }
        
        try {
            // 检查用户名是否存在
            if (userDao.usernameExists(username.trim())) {
                return new LoginResult(false, "用户名已存在", null);
            }
            
            // 检查邮箱是否存在
            if (userDao.emailExists(email.trim())) {
                return new LoginResult(false, "邮箱已存在", null);
            }
            
            // 使用工厂创建普通用户
            User newUser = userFactory.createNormalUser(username, password, email);
            
            // 保存用户到数据库
            boolean success = userDao.addUser(newUser);
            if (success) {
                // 重新获取用户（包含数据库生成的ID）
                User savedUser = userDao.getUserByUsername(username.trim());
                return new LoginResult(true, "注册成功", savedUser);
            } else {
                return new LoginResult(false, "注册失败，请重试", null);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new LoginResult(false, "系统错误，注册失败", null);
        }
    }
    
    @Override
    public LoginResult registerAdmin(String username, String password, String email, String adminKey) {
        if (ValidationUtil.isEmpty(username) || ValidationUtil.isEmpty(password) 
            || ValidationUtil.isEmpty(email) || ValidationUtil.isEmpty(adminKey)) {
            return new LoginResult(false, "管理员信息不能为空", null);
        }
        
        // 验证管理员密钥
        if (!ADMIN_KEY.equals(adminKey)) {
            return new LoginResult(false, "管理员密钥错误", null);
        }
        
        if (!ValidationUtil.isValidEmail(email)) {
            return new LoginResult(false, "邮箱格式不正确", null);
        }
        
        try {
            // 检查用户名是否存在
            if (userDao.usernameExists(username.trim())) {
                return new LoginResult(false, "用户名已存在", null);
            }
            
            // 检查邮箱是否存在
            if (userDao.emailExists(email.trim())) {
                return new LoginResult(false, "邮箱已存在", null);
            }
            
            // 使用工厂创建管理员用户
            User adminUser = userFactory.createAdmin(username, password, email);
            
            // 保存用户到数据库
            boolean success = userDao.addUser(adminUser);
            if (success) {
                // 重新获取用户（包含数据库生成的ID）
                User savedAdmin = userDao.getUserByUsername(username.trim());
                return new LoginResult(true, "管理员注册成功", savedAdmin);
            } else {
                return new LoginResult(false, "注册失败，请重试", null);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new LoginResult(false, "系统错误，注册失败", null);
        }
    }
    
    @Override
    public boolean logout(int userId) {
        if (userId <= 0) {
            return false;
        }
        
        try {
            // 更新最后登录时间（可选，记录用户活跃时间）
            userDao.updateLastLogin(userId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean hasPermission(int userId, User.UserRole requiredRole) {
        if (userId <= 0 || requiredRole == null) {
            return false;
        }
        
        try {
            User user = userDao.getUserById(userId);
            if (user == null || user.getStatus() != User.UserStatus.ACTIVE) {
                return false;
            }
            
            // 权限级别检查：ADMIN > MODERATOR > USER
            switch (requiredRole) {
                case ADMIN:
                    return user.getRole() == User.UserRole.ADMIN;
                case MODERATOR:
                    return user.getRole() == User.UserRole.ADMIN || user.getRole() == User.UserRole.MODERATOR;
                case USER:
                    return true; // 所有激活用户都有基本权限
                default:
                    return false;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean isUserBanned(int userId) {
        if (userId <= 0) {
            return true; // 无效用户ID视为被封禁
        }
        
        try {
            User user = userDao.getUserById(userId);
            return user == null || user.getStatus() == User.UserStatus.BANNED;
        } catch (Exception e) {
            e.printStackTrace();
            return true; // 发生异常时视为被封禁，确保安全
        }
    }
    
    @Override
    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        if (userId <= 0 || ValidationUtil.isEmpty(oldPassword) || ValidationUtil.isEmpty(newPassword)) {
            return false;
        }
        
        try {
            User user = userDao.getUserById(userId);
            if (user == null) {
                return false;
            }
            
            // 验证旧密码
            if (!PasswordUtil.verify(oldPassword, user.getPassword())) {
                return false;
            }
            
            // 加密新密码并更新
            String encryptedNewPassword = PasswordUtil.encrypt(newPassword);
            return userDao.changePassword(userId, encryptedNewPassword);
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
