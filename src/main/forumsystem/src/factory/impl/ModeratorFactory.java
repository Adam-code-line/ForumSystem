package main.forumsystem.src.factory.impl;

import main.forumsystem.src.entity.User;
import main.forumsystem.src.factory.impl.UserFactoryImpl;

/**
 * 版主工厂类
 * 专门用于创建版主相关对象
 */
public class ModeratorFactory {
    
    private static final UserFactoryImpl userFactory = new UserFactoryImpl();
    
    /**
     * 创建版主用户
     */
    public static User createModerator(String username, String password, String email) {
        return userFactory.createModerator(username, password, email);
    }
    
    /**
     * 将普通用户提升为版主
     */
    public static User promoteToModerator(User user) {
        if (user == null) {
            throw new IllegalArgumentException("用户不能为空");
        }
        
        user.setRole(User.UserRole.MODERATOR);
        return user;
    }
}
