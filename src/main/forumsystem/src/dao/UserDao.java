package main.forumsystem.src.dao;

import main.forumsystem.src.entity.User;
import java.util.List;

/**
 * 用户数据访问接口
 * 定义所有与用户相关的数据库操作
 */
public interface UserDao {
    
    /**
     * 添加新用户
     * @param user 用户对象
     * @return 是否添加成功
     */
    boolean addUser(User user);
    
    /**
     * 根据用户ID删除用户
     * @param userId 用户ID
     * @return 是否删除成功
     */
    boolean deleteUser(int userId);
    
    /**
     * 更新用户信息
     * @param user 用户对象
     * @return 是否更新成功
     */
    boolean updateUser(User user);
    
    /**
     * 根据用户ID查询用户
     * @param userId 用户ID
     * @return 用户对象，如果不存在返回null
     */
    User getUserById(int userId);
    
    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户对象，如果不存在返回null
     */
    User getUserByUsername(String username);
    
    /**
     * 根据邮箱查询用户
     * @param email 邮箱
     * @return 用户对象，如果不存在返回null
     */
    User getUserByEmail(String email);
    
    /**
     * 验证用户登录
     * @param username 用户名
     * @param password 密码
     * @return 用户对象，如果验证失败返回null
     */
    User validateLogin(String username, String password);
    
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
     * 获取所有用户列表
     * @return 用户列表
     */
    List<User> getAllUsers();
    
    /**
     * 分页查询用户
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @return 用户列表
     */
    List<User> getUsersByPage(int page, int size);
    
    /**
     * 根据角色查询用户
     * @param role 用户角色
     * @return 用户列表
     */
    List<User> getUsersByRole(User.UserRole role);
    
    /**
     * 根据状态查询用户
     * @param status 用户状态
     * @return 用户列表
     */
    List<User> getUsersByStatus(User.UserStatus status);
    
    /**
     * 更新用户最后登录时间
     * @param userId 用户ID
     * @return 是否更新成功
     */
    boolean updateLastLogin(int userId);
    
    /**
     * 更新用户帖子数量
     * @param userId 用户ID
     * @param increment 增加的数量（可以为负数）
     * @return 是否更新成功
     */
    boolean updatePostCount(int userId, int increment);
    
    /**
     * 更新用户声誉值
     * @param userId 用户ID
     * @param increment 增加的声誉值（可以为负数）
     * @return 是否更新成功
     */
    boolean updateReputation(int userId, int increment);
    
    /**
     * 修改用户密码
     * @param userId 用户ID
     * @param newPassword 新密码
     * @return 是否修改成功
     */
    boolean changePassword(int userId, String newPassword);
    
    /**
     * 修改用户状态
     * @param userId 用户ID
     * @param status 新状态
     * @return 是否修改成功
     */
    boolean changeUserStatus(int userId, User.UserStatus status);
    
    /**
     * 修改用户角色
     * @param userId 用户ID
     * @param role 新角色
     * @return 是否修改成功
     */
    boolean changeUserRole(int userId, User.UserRole role);
    
    /**
     * 搜索用户（根据用户名或昵称）
     * @param keyword 搜索关键词
     * @return 用户列表
     */
    List<User> searchUsers(String keyword);
    
    /**
     * 获取用户总数
     * @return 用户总数
     */
    int getUserCount();
    
    /**
     * 获取活跃用户数
     * @return 活跃用户数
     */
    int getActiveUserCount();
    
    /**
     * 获取今日注册用户数
     * @return 今日注册用户数
     */
    int getTodayRegisterCount();
    
    /**
     * 批量删除用户
     * @param userIds 用户ID数组
     * @return 删除成功的用户数量
     */
    int batchDeleteUsers(int[] userIds);
}
