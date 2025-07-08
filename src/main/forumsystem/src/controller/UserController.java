package main.forumsystem.src.controller;

import main.forumsystem.src.service.UserService;
import main.forumsystem.src.service.LoginService;
import main.forumsystem.src.service.impl.UserServiceImpl;
import main.forumsystem.src.service.impl.LoginServiceImpl;
import main.forumsystem.src.entity.User;

import java.util.List;

/**
 * 用户控制器
 * 负责权限验证、参数校验，调用Service处理业务逻辑
 */
public class UserController {
    
    private final UserService userService;
    private final LoginService loginService;
    
    public UserController() {
        this.userService = new UserServiceImpl();
        this.loginService = new LoginServiceImpl();
    }
    
    // ==================== 权限验证辅助方法 ====================
    
    private boolean verifyAdminPermission(int operatorId) {
        return loginService.hasPermission(operatorId, User.UserRole.ADMIN);
    }
    
    private boolean verifyModeratorPermission(int operatorId) {
        return loginService.hasPermission(operatorId, User.UserRole.MODERATOR);
    }
    
    private boolean verifyNotBanned(int userId) {
        return !loginService.isUserBanned(userId);
    }
    
    // ==================== 用户信息管理 ====================
    
    /**
     * 获取用户详细信息
     */
    public User getUserById(int userId) {
        if (userId <= 0) {
            return null;
        }
        return userService.getUserById(userId);
    }
    
    /**
     * 更新用户个人信息
     */
    public UserService.UserResult updateUserProfile(int operatorId, User user) {
        if (!verifyNotBanned(operatorId)) {
            return new UserService.UserResult(false, "您已被封禁，无法修改信息");
        }
        
        if (user == null || user.getUserId() != operatorId) {
            return new UserService.UserResult(false, "只能修改自己的信息");
        }
        
        return userService.updateUserProfile(user);
    }
    
    /**
     * 更新用户昵称
     */
    public UserService.UserResult updateUserNickName(int operatorId, int targetUserId, String nickName) {
        if (!verifyNotBanned(operatorId)) {
            return new UserService.UserResult(false, "您已被封禁，无法修改信息");
        }
        
        // 只能修改自己的昵称，或者管理员可以修改任何人的昵称
        if (operatorId != targetUserId && !verifyAdminPermission(operatorId)) {
            return new UserService.UserResult(false, "只能修改自己的昵称");
        }
        
        if (nickName == null || nickName.trim().isEmpty()) {
            return new UserService.UserResult(false, "昵称不能为空");
        }
        
        return userService.updateUserNickName(targetUserId, nickName.trim());
    }
    
    // ==================== 用户权限管理（需要管理员权限） ====================
    
    /**
     * 提升用户为版主
     */
    public UserService.UserResult promoteToModerator(int operatorId, int targetUserId) {
        if (!verifyAdminPermission(operatorId)) {
            return new UserService.UserResult(false, "需要管理员权限");
        }
        
        if (targetUserId <= 0) {
            return new UserService.UserResult(false, "目标用户ID无效");
        }
        
        return userService.promoteToModerator(targetUserId);
    }
    
    /**
     * 降级版主为普通用户
     */
    public UserService.UserResult demoteToUser(int operatorId, int targetUserId) {
        if (!verifyAdminPermission(operatorId)) {
            return new UserService.UserResult(false, "需要管理员权限");
        }
        
        return userService.demoteToUser(targetUserId);
    }
    
    /**
     * 提升用户为管理员
     */
    public UserService.UserResult promoteToAdmin(int operatorId, int targetUserId) {
        if (!verifyAdminPermission(operatorId)) {
            return new UserService.UserResult(false, "需要管理员权限");
        }
        
        return userService.promoteToAdmin(targetUserId);
    }
    
    // ==================== 用户状态管理（需要管理员权限） ====================
    
    /**
     * 封禁用户
     */
    public UserService.UserResult banUser(int operatorId, int targetUserId) {
        if (!verifyAdminPermission(operatorId)) {
            return new UserService.UserResult(false, "需要管理员权限");
        }
        
        if (operatorId == targetUserId) {
            return new UserService.UserResult(false, "不能封禁自己");
        }
        
        return userService.banUser(targetUserId);
    }
    
    /**
     * 解封用户
     */
    public UserService.UserResult unbanUser(int operatorId, int targetUserId) {
        if (!verifyAdminPermission(operatorId)) {
            return new UserService.UserResult(false, "需要管理员权限");
        }
        
        return userService.unbanUser(targetUserId);
    }
    
    // ==================== 用户删除管理（需要管理员权限） ====================
    
    /**
     * 删除用户
     */
    public UserService.UserResult deleteUser(int operatorId, int targetUserId) {
        if (!verifyAdminPermission(operatorId)) {
            return new UserService.UserResult(false, "需要管理员权限");
        }
        
        if (operatorId == targetUserId) {
            return new UserService.UserResult(false, "不能删除自己");
        }
        
        return userService.deleteUser(targetUserId);
    }
    
    /**
     * 批量删除用户
     */
    public BatchResult batchDeleteUsers(int operatorId, int[] targetUserIds) {
        if (!verifyAdminPermission(operatorId)) {
            return new BatchResult(false, "需要管理员权限", 0);
        }
        
        if (targetUserIds == null || targetUserIds.length == 0) {
            return new BatchResult(false, "用户ID列表不能为空", 0);
        }
        
        // 过滤掉操作者自己
        List<Integer> validIds = new java.util.ArrayList<>();
        for (int userId : targetUserIds) {
            if (userId != operatorId && userId > 0) {
                validIds.add(userId);
            }
        }
        
        if (validIds.isEmpty()) {
            return new BatchResult(false, "没有有效的用户ID", 0);
        }
        
        int[] filteredIds = validIds.stream().mapToInt(Integer::intValue).toArray();
        int deletedCount = userService.batchDeleteUsers(filteredIds);
        
        return new BatchResult(deletedCount > 0, 
                              deletedCount > 0 ? "批量删除成功" : "批量删除失败", 
                              deletedCount);
    }
    
    // ==================== 用户声誉管理（需要版主以上权限） ====================
    
    /**
     * 增加用户声誉值
     */
    public boolean increaseReputation(int operatorId, int targetUserId, int increment) {
        if (!verifyModeratorPermission(operatorId)) {
            return false;
        }
        
        if (targetUserId <= 0 || increment <= 0) {
            return false;
        }
        
        return userService.increaseReputation(targetUserId, increment);
    }
    
    /**
     * 减少用户声誉值
     */
    public boolean decreaseReputation(int operatorId, int targetUserId, int decrement) {
        if (!verifyModeratorPermission(operatorId)) {
            return false;
        }
        
        if (targetUserId <= 0 || decrement <= 0) {
            return false;
        }
        
        return userService.decreaseReputation(targetUserId, decrement);
    }
    
    // ==================== 查询功能（无需特殊权限） ====================
    
    /**
     * 获取用户列表（分页）
     */
    public List<User> getUsersByPage(int page, int size) {
        if (page <= 0) page = 1;
        if (size <= 0 || size > 100) size = 10;
        
        return userService.getUsersByPage(page, size);
    }
    
    /**
     * 搜索用户
     */
    public List<User> searchUsers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        
        return userService.searchUsers(keyword.trim());
    }
    
    /**
     * 获取用户统计信息（需要管理员权限）
     */
    public UserService.UserStatistics getUserStatistics(int operatorId) {
        if (!verifyAdminPermission(operatorId)) {
            return new UserService.UserStatistics(0, 0, 0, 0, 0, 0);
        }
        
        return userService.getUserStatistics();
    }
    
    // ==================== 权限检查方法 ====================
    
    /**
     * 检查用户是否有管理权限
     */
    public boolean hasAdminPermission(int userId) {
        return verifyAdminPermission(userId);
    }
    
    /**
     * 检查用户是否有版主权限
     */
    public boolean hasModeratorPermission(int userId) {
        return verifyModeratorPermission(userId);
    }
    
    /**
     * 检查用户是否被封禁
     */
    public boolean isUserBanned(int userId) {
        return loginService.isUserBanned(userId);
    }
    
    // ==================== 结果类 ====================
    
    /**
     * 批量操作结果类
     */
    public static class BatchResult {
        private boolean success;
        private String message;
        private int affectedCount;
        
        public BatchResult(boolean success, String message, int affectedCount) {
            this.success = success;
            this.message = message;
            this.affectedCount = affectedCount;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public int getAffectedCount() { return affectedCount; }
    }
}
