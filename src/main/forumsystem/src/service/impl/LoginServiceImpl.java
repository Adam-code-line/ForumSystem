package main.forumsystem.src.service.impl;

import main.forumsystem.src.dao.UserDao;
import main.forumsystem.src.dao.impl.UserDaoImpl;
import main.forumsystem.src.entity.User;
import main.forumsystem.src.service.LoginService;
import main.forumsystem.src.util.PasswordUtil;

import java.time.LocalDateTime;

/**
 * 登录服务实现类
 */
public class LoginServiceImpl implements LoginService {
    
    private final UserDao userDao;
    private static final String ADMIN_KEY = "ADMIN_SECRET_KEY_2024"; // 管理员注册密钥
    
    public LoginServiceImpl() {
        this.userDao = new UserDaoImpl();
    }
    
    @Override
    public LoginResult login(String username, String password) {
        // 输入验证
        if (username == null || username.trim().isEmpty()) {
            return new LoginResult(false, "用户名不能为空", null);
        }
        if (password == null || password.trim().isEmpty()) {
            return new LoginResult(false, "密码不能为空", null);
        }
        
        try {
            // 验证用户登录
            User user = userDao.validateLogin(username, PasswordUtil.encrypt(password));
            if (user == null) {
                return new LoginResult(false, "用户名或密码错误", null);
            }
            
            // 检查用户状态
            if (user.getStatus() == User.UserStatus.BANNED) {
                return new LoginResult(false, "账户已被封禁，无法登录", null);
            }
            
            if (user.getStatus() == User.UserStatus.INACTIVE) {
                return new LoginResult(false, "账户未激活，请先激活账户", null);
            }
            
            // 更新最后登录时间
            userDao.updateLastLogin(user.getUserId());
            
            return new LoginResult(true, "登录成功", user);
            
        } catch (Exception e) {
            e.printStackTrace();
            return new LoginResult(false, "登录失败，系统错误", null);
        }
    }
    
    @Override
    public RegisterResult register(String username, String password, String email) {
        // 输入验证
        if (username == null || username.trim().isEmpty()) {
            return new RegisterResult(false, "用户名不能为空", -1);
        }
        if (password == null || password.length() < 6) {
            return new RegisterResult(false, "密码长度不能少于6位", -1);
        }
        if (email == null || !isValidEmail(email)) {
            return new RegisterResult(false, "邮箱格式不正确", -1);
        }
        
        try {
            // 检查用户名是否已存在
            if (userDao.usernameExists(username)) {
                return new RegisterResult(false, "用户名已存在", -1);
            }
            
            // 检查邮箱是否已存在
            if (userDao.emailExists(email)) {
                return new RegisterResult(false, "邮箱已被注册", -1);
            }
            
            // 创建新用户
            User newUser = new User(username, PasswordUtil.encrypt(password), email);
            newUser.setRole(User.UserRole.USER);
            newUser.setStatus(User.UserStatus.ACTIVE);
            newUser.setRegisterTime(LocalDateTime.now());
            
            // 保存用户
            boolean success = userDao.addUser(newUser);
            if (success) {
                // 获取新用户ID
                User savedUser = userDao.getUserByUsername(username);
                return new RegisterResult(true, "注册成功", savedUser.getUserId());
            } else {
                return new RegisterResult(false, "注册失败，请重试", -1);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new RegisterResult(false, "注册失败，系统错误", -1);
        }
    }
    
    @Override
    public RegisterResult registerAdmin(String username, String password, String email, String adminKey) {
        // 验证管理员密钥
        if (!ADMIN_KEY.equals(adminKey)) {
            return new RegisterResult(false, "管理员密钥错误", -1);
        }
        
        // 输入验证
        if (username == null || username.trim().isEmpty()) {
            return new RegisterResult(false, "用户名不能为空", -1);
        }
        if (password == null || password.length() < 6) {
            return new RegisterResult(false, "密码长度不能少于6位", -1);
        }
        if (email == null || !isValidEmail(email)) {
            return new RegisterResult(false, "邮箱格式不正确", -1);
        }
        
        try {
            // 检查用户名是否已存在
            if (userDao.usernameExists(username)) {
                return new RegisterResult(false, "用户名已存在", -1);
            }
            
            // 检查邮箱是否已存在
            if (userDao.emailExists(email)) {
                return new RegisterResult(false, "邮箱已被注册", -1);
            }
            
            // 创建新管理员用户
            User newAdmin = new User(username, PasswordUtil.encrypt(password), email);
            newAdmin.setRole(User.UserRole.ADMIN);
            newAdmin.setStatus(User.UserStatus.ACTIVE);
            newAdmin.setRegisterTime(LocalDateTime.now());
            
            // 保存管理员
            boolean success = userDao.addUser(newAdmin);
            if (success) {
                // 获取新用户ID
                User savedUser = userDao.getUserByUsername(username);
                return new RegisterResult(true, "管理员注册成功", savedUser.getUserId());
            } else {
                return new RegisterResult(false, "管理员注册失败，请重试", -1);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new RegisterResult(false, "管理员注册失败，系统错误", -1);
        }
    }
    
    @Override
    public boolean logout(int userId) {
        try {
            // 这里可以添加登出逻辑，比如清除session、记录登出时间等
            // 目前只是简单返回true
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean hasPermission(int userId, User.UserRole requiredRole) {
        try {
            User user = userDao.getUserById(userId);
            if (user == null) {
                return false;
            }
            
            // 检查用户是否被封禁
            if (user.getStatus() == User.UserStatus.BANNED) {
                return false;
            }
            
            // 权限层级：ADMIN > MODERATOR > USER
            User.UserRole userRole = user.getRole();
            
            switch (requiredRole) {
                case USER:
                    return userRole == User.UserRole.USER || 
                           userRole == User.UserRole.MODERATOR || 
                           userRole == User.UserRole.ADMIN;
                case MODERATOR:
                    return userRole == User.UserRole.MODERATOR || 
                           userRole == User.UserRole.ADMIN;
                case ADMIN:
                    return userRole == User.UserRole.ADMIN;
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
        try {
            User user = userDao.getUserById(userId);
            return user != null && user.getStatus() == User.UserStatus.BANNED;
        } catch (Exception e) {
            e.printStackTrace();
            return true; // 出错时默认认为用户被封禁
        }
    }
    
    @Override
    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        // 输入验证
        if (oldPassword == null || oldPassword.trim().isEmpty()) {
            return false;
        }
        if (newPassword == null || newPassword.length() < 6) {
            return false;
        }
        
        try {
            // 获取用户信息
            User user = userDao.getUserById(userId);
            if (user == null) {
                return false;
            }
            
            // 验证旧密码
            if (!PasswordUtil.verify(oldPassword, user.getPassword())) {
                return false;
            }
            
            // 更新密码
            return userDao.changePassword(userId, PasswordUtil.encrypt(newPassword));
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 验证邮箱格式
     */
    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-ZaZ0-9.-]+\\.[A-Za-z]{2,}$");
    }
}
