package main.forumsystem.src.service.impl;

import main.forumsystem.src.dao.UserDao;
import main.forumsystem.src.dao.impl.UserDaoImpl;
import main.forumsystem.src.entity.User;
import main.forumsystem.src.service.UserService;
import main.forumsystem.src.util.ValidationUtil;

import java.util.List;

/**
 * 用户服务实现类
 * 专注于用户管理、用户信息查询、用户权限管理等核心业务
 */
public class UserServiceImpl implements UserService {
    
    private final UserDao userDao;
    
    public UserServiceImpl() {
        this.userDao = new UserDaoImpl();
    }

    @Override
    public String getUserName(int userId) {
        User user = getUserById(userId);
        return user != null ? user.getUsername() : "未知用户";
    }

    @Override
    public User getUserById(int userId) {
        if (userId <= 0) {
            return null;
        }
        
        try {
            return userDao.getUserById(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public User getUserByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        
        try {
            return userDao.getUserByUsername(username.trim());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public User getUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        
        try {
            return userDao.getUserByEmail(email.trim());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public UserResult updateUserProfile(User user) {
        if (user == null) {
            return new UserResult(false, "用户信息不能为空");
        }
        
        // 验证用户ID
        if (user.getUserId() <= 0) {
            return new UserResult(false, "用户ID无效");
        }
        
        // 验证用户名
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return new UserResult(false, "用户名不能为空");
        }
        
        // 验证邮箱格式
        if (user.getEmail() == null || !ValidationUtil.isValidEmail(user.getEmail())) {
            return new UserResult(false, "邮箱格式不正确");
        }
        
        try {
            // 检查用户是否存在
            User existingUser = userDao.getUserById(user.getUserId());
            if (existingUser == null) {
                return new UserResult(false, "用户不存在");
            }
            
            // 检查用户名是否被其他用户使用
            User userWithSameName = userDao.getUserByUsername(user.getUsername());
            if (userWithSameName != null && userWithSameName.getUserId() != user.getUserId()) {
                return new UserResult(false, "用户名已被其他用户使用");
            }
            
            // 检查邮箱是否被其他用户使用
            User userWithSameEmail = userDao.getUserByEmail(user.getEmail());
            if (userWithSameEmail != null && userWithSameEmail.getUserId() != user.getUserId()) {
                return new UserResult(false, "邮箱已被其他用户使用");
            }
            
            // 更新用户信息
            boolean success = userDao.updateUser(user);
            if (success) {
                User updatedUser = userDao.getUserById(user.getUserId());
                return new UserResult(true, "用户信息更新成功", updatedUser);
            } else {
                return new UserResult(false, "更新失败，请重试");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new UserResult(false, "系统错误，更新失败");
        }
    }
    
    @Override
    public boolean updateUserAvatar(int userId, String avatarUrl) {
        if (userId <= 0) {
            return false;
        }
        
        try {
            User user = userDao.getUserById(userId);
            if (user == null) {
                return false;
            }
            
            user.setAvatar(avatarUrl);
            return userDao.updateUser(user);
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public UserResult updateUserNickName(int userId, String nickName) {
        if (userId <= 0) {
            return new UserResult(false, "用户ID无效");
        }
        
        if (nickName == null || nickName.trim().isEmpty()) {
            return new UserResult(false, "昵称不能为空");
        }
        
        if (nickName.length() > 20) {
            return new UserResult(false, "昵称长度不能超过20个字符");
        }
        
        try {
            User user = userDao.getUserById(userId);
            if (user == null) {
                return new UserResult(false, "用户不存在");
            }
            
            user.setNickName(nickName.trim());
            boolean success = userDao.updateUser(user);
            
            if (success) {
                User updatedUser = userDao.getUserById(userId);
                return new UserResult(true, "昵称更新成功", updatedUser);
            } else {
                return new UserResult(false, "更新失败，请重试");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new UserResult(false, "系统错误，更新失败");
        }
    }
    
    @Override
    public List<User> getUsersByPage(int page, int size) {
        if (page <= 0) {
            page = 1;
        }
        if (size <= 0 || size > 100) {
            size = 10;
        }
        
        try {
            return userDao.getUsersByPage(page, size);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
    
    @Override
    public List<User> searchUsers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        
        try {
            return userDao.searchUsers(keyword.trim());
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
    
    @Override
    public List<User> getUsersByRole(User.UserRole role) {
        if (role == null) {
            return List.of();
        }
        
        try {
            return userDao.getUsersByRole(role);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
    
    @Override
    public List<User> getUsersByStatus(User.UserStatus status) {
        if (status == null) {
            return List.of();
        }
        
        try {
            return userDao.getUsersByStatus(status);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
    
    @Override
    public List<User> getAllAdmins() {
        try {
            return userDao.getUsersByRole(User.UserRole.ADMIN);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
    
    @Override
    public List<User> getAllModerators() {
        try {
            return userDao.getUsersByRole(User.UserRole.MODERATOR);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
    
    @Override
    public boolean increasePostCount(int userId, int increment) {
        if (userId <= 0 || increment <= 0) {
            return false;
        }
        
        try {
            return userDao.updatePostCount(userId, increment);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean decreasePostCount(int userId, int decrement) {
        if (userId <= 0 || decrement <= 0) {
            return false;
        }
        
        try {
            return userDao.updatePostCount(userId, -decrement);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean increaseReputation(int userId, int increment) {
        if (userId <= 0 || increment <= 0) {
            return false;
        }
        
        try {
            return userDao.updateReputation(userId, increment);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean decreaseReputation(int userId, int decrement) {
        if (userId <= 0 || decrement <= 0) {
            return false;
        }
        
        try {
            return userDao.updateReputation(userId, -decrement);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public UserResult promoteToModerator(int userId) {
        if (userId <= 0) {
            return new UserResult(false, "用户ID无效");
        }
        
        try {
            User user = userDao.getUserById(userId);
            if (user == null) {
                return new UserResult(false, "用户不存在");
            }
            
            if (user.getRole() == User.UserRole.ADMIN) {
                return new UserResult(false, "管理员不能被设置为版主");
            }
            
            if (user.getRole() == User.UserRole.MODERATOR) {
                return new UserResult(false, "用户已经是版主");
            }
            
            if (user.getStatus() == User.UserStatus.BANNED) {
                return new UserResult(false, "被封禁的用户不能提升为版主");
            }
            
            boolean success = userDao.changeUserRole(userId, User.UserRole.MODERATOR);
            if (success) {
                User updatedUser = userDao.getUserById(userId);
                return new UserResult(true, "用户已提升为版主", updatedUser);
            } else {
                return new UserResult(false, "提升失败，请重试");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new UserResult(false, "系统错误，提升失败");
        }
    }
    
    @Override
    public UserResult demoteToUser(int userId) {
        if (userId <= 0) {
            return new UserResult(false, "用户ID无效");
        }
        
        try {
            User user = userDao.getUserById(userId);
            if (user == null) {
                return new UserResult(false, "用户不存在");
            }
            
            if (user.getRole() == User.UserRole.ADMIN) {
                return new UserResult(false, "管理员不能被降级");
            }
            
            if (user.getRole() == User.UserRole.USER) {
                return new UserResult(false, "用户已经是普通用户");
            }
            
            boolean success = userDao.changeUserRole(userId, User.UserRole.USER);
            if (success) {
                User updatedUser = userDao.getUserById(userId);
                return new UserResult(true, "用户已降级为普通用户", updatedUser);
            } else {
                return new UserResult(false, "降级失败，请重试");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new UserResult(false, "系统错误，降级失败");
        }
    }
    
    @Override
    public UserResult promoteToAdmin(int userId) {
        if (userId <= 0) {
            return new UserResult(false, "用户ID无效");
        }
        
        try {
            User user = userDao.getUserById(userId);
            if (user == null) {
                return new UserResult(false, "用户不存在");
            }
            
            if (user.getRole() == User.UserRole.ADMIN) {
                return new UserResult(false, "用户已经是管理员");
            }
            
            if (user.getStatus() == User.UserStatus.BANNED) {
                return new UserResult(false, "被封禁的用户不能提升为管理员");
            }
            
            boolean success = userDao.changeUserRole(userId, User.UserRole.ADMIN);
            if (success) {
                User updatedUser = userDao.getUserById(userId);
                return new UserResult(true, "用户已提升为管理员", updatedUser);
            } else {
                return new UserResult(false, "提升失败，请重试");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new UserResult(false, "系统错误，提升失败");
        }
    }
    
    @Override
    public UserResult banUser(int userId) {
        if (userId <= 0) {
            return new UserResult(false, "用户ID无效");
        }
        
        try {
            User user = userDao.getUserById(userId);
            if (user == null) {
                return new UserResult(false, "用户不存在");
            }
            
            if (user.getRole() == User.UserRole.ADMIN) {
                return new UserResult(false, "管理员不能被封禁");
            }
            
            if (user.getStatus() == User.UserStatus.BANNED) {
                return new UserResult(false, "用户已被封禁");
            }
            
            boolean success = userDao.changeUserStatus(userId, User.UserStatus.BANNED);
            if (success) {
                User updatedUser = userDao.getUserById(userId);
                return new UserResult(true, "用户已被封禁", updatedUser);
            } else {
                return new UserResult(false, "封禁失败，请重试");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new UserResult(false, "系统错误，封禁失败");
        }
    }
    
    @Override
    public UserResult unbanUser(int userId) {
        if (userId <= 0) {
            return new UserResult(false, "用户ID无效");
        }
        
        try {
            User user = userDao.getUserById(userId);
            if (user == null) {
                return new UserResult(false, "用户不存在");
            }
            
            if (user.getStatus() != User.UserStatus.BANNED) {
                return new UserResult(false, "用户未被封禁");
            }
            
            boolean success = userDao.changeUserStatus(userId, User.UserStatus.ACTIVE);
            if (success) {
                User updatedUser = userDao.getUserById(userId);
                return new UserResult(true, "用户已解封", updatedUser);
            } else {
                return new UserResult(false, "解封失败，请重试");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new UserResult(false, "系统错误，解封失败");
        }
    }
    
    @Override
    public UserResult activateUser(int userId) {
        if (userId <= 0) {
            return new UserResult(false, "用户ID无效");
        }
        
        try {
            User user = userDao.getUserById(userId);
            if (user == null) {
                return new UserResult(false, "用户不存在");
            }
            
            if (user.getStatus() == User.UserStatus.ACTIVE) {
                return new UserResult(false, "用户已激活");
            }
            
            boolean success = userDao.changeUserStatus(userId, User.UserStatus.ACTIVE);
            if (success) {
                User updatedUser = userDao.getUserById(userId);
                return new UserResult(true, "用户已激活", updatedUser);
            } else {
                return new UserResult(false, "激活失败，请重试");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new UserResult(false, "系统错误，激活失败");
        }
    }
    
    @Override
    public boolean userExists(int userId) {
        if (userId <= 0) {
            return false;
        }
        
        try {
            return userDao.getUserById(userId) != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean usernameExists(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        
        try {
            return userDao.usernameExists(username.trim());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean emailExists(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        try {
            return userDao.emailExists(email.trim());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public UserStatistics getUserStatistics() {
        try {
            int totalUsers = userDao.getUserCount();
            int activeUsers = userDao.getActiveUserCount();
            int todayRegisterCount = userDao.getTodayRegisterCount();
            
            List<User> admins = userDao.getUsersByRole(User.UserRole.ADMIN);
            List<User> moderators = userDao.getUsersByRole(User.UserRole.MODERATOR);
            List<User> bannedUsers = userDao.getUsersByStatus(User.UserStatus.BANNED);
            
            return new UserStatistics(
                totalUsers, 
                activeUsers, 
                bannedUsers.size(), 
                todayRegisterCount,
                admins.size(),
                moderators.size()
            );
            
        } catch (Exception e) {
            e.printStackTrace();
            return new UserStatistics(0, 0, 0, 0, 0, 0);
        }
    }
    
    @Override
    public UserResult deleteUser(int userId) {
        if (userId <= 0) {
            return new UserResult(false, "用户ID无效");
        }
        
        try {
            User user = userDao.getUserById(userId);
            if (user == null) {
                return new UserResult(false, "用户不存在");
            }
            
            if (user.getRole() == User.UserRole.ADMIN) {
                return new UserResult(false, "管理员不能被删除");
            }
            
            boolean success = userDao.deleteUser(userId);
            if (success) {
                return new UserResult(true, "用户已删除");
            } else {
                return new UserResult(false, "删除失败，请重试");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new UserResult(false, "系统错误，删除失败");
        }
    }
    
    @Override
    public int batchDeleteUsers(int[] userIds) {
        if (userIds == null || userIds.length == 0) {
            return 0;
        }
        
        try {
            // 过滤掉管理员用户
            List<Integer> validUserIds = new java.util.ArrayList<>();
            for (int userId : userIds) {
                User user = userDao.getUserById(userId);
                if (user != null && user.getRole() != User.UserRole.ADMIN) {
                    validUserIds.add(userId);
                }
            }
            
            if (validUserIds.isEmpty()) {
                return 0;
            }
            
            int[] validIds = validUserIds.stream().mapToInt(Integer::intValue).toArray();
            return userDao.batchDeleteUsers(validIds);
            
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
