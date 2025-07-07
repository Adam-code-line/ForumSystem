package main.forumsystem.src.test;

import main.forumsystem.src.dao.UserDao;
import main.forumsystem.src.dao.impl.UserDaoImpl;
import main.forumsystem.src.entity.User;

import java.util.List;

/**
 * UserDao 测试类
 */
public class UserDaoTest {
    
    public static void main(String[] args) {
        UserDao userDao = new UserDaoImpl();
        
        System.out.println("=== UserDao 基本功能测试开始 ===\n");
        
        // 1. 测试添加用户
        System.out.println("1. 测试添加用户:");
        User newUser = new User();
        newUser.setUsername("testuser001");
        newUser.setPassword("123456");
        newUser.setEmail("testuser001@example.com");
        newUser.setNickName("测试用户001");
        newUser.setRole(User.UserRole.USER);
        newUser.setStatus(User.UserStatus.ACTIVE);
        
        boolean addResult = userDao.addUser(newUser);
        System.out.println("   添加用户结果: " + (addResult ? "✅ 成功" : "❌ 失败"));
        
        // 2. 测试用户名是否存在
        System.out.println("\n2. 测试用户名重复检查:");
        boolean usernameExists = userDao.usernameExists("testuser001");
        System.out.println("   用户名 'testuser001' 是否存在: " + (usernameExists ? "✅ 存在" : "❌ 不存在"));
        
        // 3. 测试根据用户名查询用户
        System.out.println("\n3. 测试根据用户名查询用户:");
        User foundUser = userDao.getUserByUsername("testuser001");
        if (foundUser != null) {
            System.out.println("   ✅ 查询成功:");
            System.out.println("      用户ID: " + foundUser.getUserId());
            System.out.println("      用户名: " + foundUser.getUsername());
            System.out.println("      昵称: " + foundUser.getNickName());
            System.out.println("      邮箱: " + foundUser.getEmail());
            System.out.println("      角色: " + foundUser.getRole().getValue());
            System.out.println("      状态: " + foundUser.getStatus().getValue());
        } else {
            System.out.println("   ❌ 查询失败: 用户不存在");
        }
        
        // 4. 测试登录验证
        System.out.println("\n4. 测试登录验证:");
        User loginUser = userDao.validateLogin("testuser001", "123456");
        System.out.println("   正确密码登录: " + (loginUser != null ? "✅ 成功" : "❌ 失败"));
        
        User wrongPasswordUser = userDao.validateLogin("testuser001", "wrongpassword");
        System.out.println("   错误密码登录: " + (wrongPasswordUser == null ? "✅ 正确阻止" : "❌ 验证失败"));
        
        // 5. 测试更新用户信息
        if (foundUser != null) {
            System.out.println("\n5. 测试更新用户信息:");
            foundUser.setNickName("修改后的昵称");
            foundUser.setEmail("newemail@example.com");
            boolean updateResult = userDao.updateUser(foundUser);
            System.out.println("   更新用户信息: " + (updateResult ? "✅ 成功" : "❌ 失败"));
            
            // 验证更新结果
            User updatedUser = userDao.getUserById(foundUser.getUserId());
            if (updatedUser != null) {
                System.out.println("   验证更新结果:");
                System.out.println("      新昵称: " + updatedUser.getNickName());
                System.out.println("      新邮箱: " + updatedUser.getEmail());
            }
        }
        
        // 6. 测试统计功能
        System.out.println("\n6. 测试统计功能:");
        int totalUsers = userDao.getUserCount();
        int activeUsers = userDao.getActiveUserCount();
        int todayRegister = userDao.getTodayRegisterCount();
        
        System.out.println("   总用户数: " + totalUsers);
        System.out.println("   活跃用户数: " + activeUsers);
        System.out.println("   今日注册数: " + todayRegister);
        
        // 7. 测试分页查询
        System.out.println("\n7. 测试分页查询:");
        List<User> usersPage1 = userDao.getUsersByPage(1, 5);
        System.out.println("   第1页用户数量: " + usersPage1.size());
        if (!usersPage1.isEmpty()) {
            System.out.println("   第一个用户: " + usersPage1.get(0).getUsername());
        }
        
        // 8. 测试搜索功能
        System.out.println("\n8. 测试搜索功能:");
        List<User> searchResults = userDao.searchUsers("test");
        System.out.println("   搜索 'test' 的结果数量: " + searchResults.size());
        
        // 9. 测试更新最后登录时间
        if (foundUser != null) {
            System.out.println("\n9. 测试更新最后登录时间:");
            boolean updateLoginResult = userDao.updateLastLogin(foundUser.getUserId());
            System.out.println("   更新最后登录时间: " + (updateLoginResult ? "✅ 成功" : "❌ 失败"));
        }
        
        // 10. 清理测试数据
        if (foundUser != null) {
            System.out.println("\n10. 清理测试数据:");
            boolean deleteResult = userDao.deleteUser(foundUser.getUserId());
            System.out.println("   删除测试用户: " + (deleteResult ? "✅ 成功" : "❌ 失败"));
        }
        
        System.out.println("\n=== UserDao 基本功能测试完成 ===");
    }
}
