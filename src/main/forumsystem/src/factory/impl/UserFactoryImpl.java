package main.forumsystem.src.factory.impl;

import main.forumsystem.src.factory.UserFactory;
import main.forumsystem.src.factory.UserOperationFactory;
import main.forumsystem.src.entity.User;
import main.forumsystem.src.util.ValidationUtil;
import main.forumsystem.src.util.PasswordUtil;

import java.time.LocalDateTime;

/**
 * 用户工厂实现类
 * 负责创建用户对象，使用密码加密工具
 */
public class UserFactoryImpl implements UserFactory {
    
    @Override
    public User createUser(String username, String password, String email, User.UserRole role) {
        if (ValidationUtil.isEmpty(username) || ValidationUtil.isEmpty(password) || ValidationUtil.isEmpty(email)) {
            throw new IllegalArgumentException("用户信息不能为空");
        }
        
        if (!ValidationUtil.isValidEmail(email)) {
            throw new IllegalArgumentException("邮箱格式不正确");
        }
        
        if (role == null) {
            role = User.UserRole.USER; // 默认为普通用户
        }
        
        User user = new User();
        user.setUsername(username.trim());
        user.setPassword(PasswordUtil.encrypt(password)); // 使用密码加密工具
        user.setEmail(email.trim());
        user.setNickName(username.trim()); // 默认昵称为用户名
        user.setRole(role);
        user.setStatus(User.UserStatus.ACTIVE);
        user.setRegisterTime(LocalDateTime.now());
        user.setLastLogin(LocalDateTime.now());
        user.setPostCount(0);
        user.setReputation(0);
        
        return user;
    }
    
    @Override
    public User createAdmin(String username, String password, String email) {
        User admin = createUser(username, password, email, User.UserRole.ADMIN);
        admin.setReputation(1000); // 管理员默认声誉值较高
        return admin;
    }
    
    @Override
    public User createModerator(String username, String password, String email) {
        User moderator = createUser(username, password, email, User.UserRole.MODERATOR);
        moderator.setReputation(500); // 版主默认声誉值中等
        return moderator;
    }
    
    @Override
    public User createNormalUser(String username, String password, String email) {
        return createUser(username, password, email, User.UserRole.USER);
    }
    
    @Override
    public UserOperationFactory getOperationFactory(User user) {
        if (user == null) {
            throw new IllegalArgumentException("用户不能为空");
        }
        
        switch (user.getRole()) {
            case ADMIN:
                return new AdminOperationFactory();
            case MODERATOR:
                return new ModeratorOperationFactory();
            case USER:
                return new NormalUserOperationFactory();
            default:
                throw new IllegalArgumentException("未知的用户角色: " + user.getRole());
        }
    }
}
