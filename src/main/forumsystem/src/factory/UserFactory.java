package main.forumsystem.src.factory;

import main.forumsystem.src.entity.User;

/**
 * 用户工厂接口
 * 负责创建不同类型的用户对象，不涉及数据库操作
 */
public interface UserFactory {
    
    /**
     * 创建用户对象（不保存到数据库）
     * @param username 用户名
     * @param password 密码
     * @param email 邮箱
     * @param role 用户角色
     * @return 用户对象
     */
    User createUser(String username, String password, String email, User.UserRole role);
    
    /**
     * 创建管理员用户
     * @param username 用户名
     * @param password 密码
     * @param email 邮箱
     * @return 管理员用户对象
     */
    User createAdmin(String username, String password, String email);
    
    /**
     * 创建版主用户
     * @param username 用户名
     * @param password 密码
     * @param email 邮箱
     * @return 版主用户对象
     */
    User createModerator(String username, String password, String email);
    
    /**
     * 创建普通用户
     * @param username 用户名
     * @param password 密码
     * @param email 邮箱
     * @return 普通用户对象
     */
    User createNormalUser(String username, String password, String email);
    
    /**
     * 根据用户角色获取对应的操作工厂
     * @param user 用户对象
     * @return 操作工厂
     */
    UserOperationFactory getOperationFactory(User user);
}
