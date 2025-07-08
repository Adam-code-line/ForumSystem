package main.forumsystem.src.factory.impl;

import main.forumsystem.src.entity.User;
import main.forumsystem.src.factory.impl.UserFactoryImpl;

/**
 * 管理员工厂类
 * 专门用于创建管理员相关对象
 */
public class AdminFactory {
    
    private static final UserFactoryImpl userFactory = new UserFactoryImpl();
    
    /**
     * 创建管理员用户
     */
    public static User createAdmin(String username, String password, String email) {
        return userFactory.createAdmin(username, password, email);
    }
    
    /**
     * 创建系统默认管理员
     */
    public static User createDefaultAdmin() {
        return userFactory.createAdmin("admin", "admin123", "admin@forum.com");
    }
}
