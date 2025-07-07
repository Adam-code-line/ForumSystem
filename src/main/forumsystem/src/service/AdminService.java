package main.forumsystem.src.service;

import main.forumsystem.src.entity.User;
import main.forumsystem.src.entity.Forum;
import main.forumsystem.src.entity.Topic;
import main.forumsystem.src.entity.Reply;
import main.forumsystem.src.entity.BanRecord;
import main.forumsystem.src.entity.SensitiveWord;
import java.util.List;
import java.util.Map;

/**
 * 管理员服务接口
 * 提供系统管理功能，包括用户管理、版主管理、板块管理、内容管理等
 */
public interface AdminService {
    
    // ==================== 用户管理 ====================
    
    /**
     * 获取所有用户列表
     * @return 用户列表
     */
    List<User> getAllUsers();
    
    /**
     * 分页查询用户
     * @param page 页码
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
     * 搜索用户
     * @param keyword 关键词（用户名、昵称、邮箱）
     * @return 用户列表
     */
    List<User> searchUsers(String keyword);
    
    /**
     * 获取用户详细信息
     * @param userId 用户ID
     * @return 用户信息
     */
    User getUserDetails(int userId);
    
    /**
     * 更新用户信息
     * @param user 用户信息
     * @return 是否更新成功
     */
    AdminResult updateUser(User user);
    
    /**
     * 修改用户角色
     * @param userId 用户ID
     * @param newRole 新角色
     * @return 操作结果
     */
    AdminResult changeUserRole(int userId, User.UserRole newRole);
    
    /**
     * 修改用户状态
     * @param userId 用户ID
     * @param newStatus 新状态
     * @return 操作结果
     */
    AdminResult changeUserStatus(int userId, User.UserStatus newStatus);
    
    /**
     * 删除用户
     * @param userId 用户ID
     * @return 操作结果
     */
    AdminResult deleteUser(int userId);
    
    /**
     * 批量删除用户
     * @param userIds 用户ID数组
     * @return 操作结果
     */
    AdminResult batchDeleteUsers(int[] userIds);
    
    /**
     * 重置用户密码
     * @param userId 用户ID
     * @param newPassword 新密码
     * @return 操作结果
     */
    AdminResult resetUserPassword(int userId, String newPassword);
    
    // ==================== 版主管理 ====================
    
    /**
     * 获取所有版主列表
     * @return 版主列表
     */
    List<User> getAllModerators();
    
    /**
     * 任命版主
     * @param userId 用户ID
     * @param forumId 板块ID（可选，0表示全局版主）
     * @return 操作结果
     */
    AdminResult appointModerator(int userId, int forumId);
    
    /**
     * 撤销版主
     * @param userId 用户ID
     * @param forumId 板块ID（可选，0表示撤销全局版主）
     * @return 操作结果
     */
    AdminResult revokeModerator(int userId, int forumId);
    
    /**
     * 获取版主管理的板块
     * @param moderatorId 版主ID
     * @return 板块列表
     */
    List<Forum> getModeratorForums(int moderatorId);
    
    // ==================== 板块管理 ====================
    
    /**
     * 获取所有板块列表
     * @return 板块列表
     */
    List<Forum> getAllForums();
    
    /**
     * 分页查询板块
     * @param page 页码
     * @param size 每页大小
     * @return 板块列表
     */
    List<Forum> getForumsByPage(int page, int size);
    
    /**
     * 根据状态查询板块
     * @param status 板块状态
     * @return 板块列表
     */
    List<Forum> getForumsByStatus(Forum.ForumStatus status);
    
    /**
     * 搜索板块
     * @param keyword 关键词
     * @return 板块列表
     */
    List<Forum> searchForums(String keyword);
    
    /**
     * 创建板块
     * @param forum 板块信息
     * @return 操作结果
     */
    AdminResult createForum(Forum forum);
    
    /**
     * 更新板块信息
     * @param forum 板块信息
     * @return 操作结果
     */
    AdminResult updateForum(Forum forum);
    
    /**
     * 删除板块
     * @param forumId 板块ID
     * @return 操作结果
     */
    AdminResult deleteForum(int forumId);
    
    /**
     * 批量删除板块
     * @param forumIds 板块ID数组
     * @return 操作结果
     */
    AdminResult batchDeleteForums(int[] forumIds);
    
    /**
     * 修改板块状态
     * @param forumId 板块ID
     * @param status 新状态
     * @return 操作结果
     */
    AdminResult changeForumStatus(int forumId, Forum.ForumStatus status);
    
    /**
     * 设置板块版主
     * @param forumId 板块ID
     * @param moderatorId 版主ID
     * @return 操作结果
     */
    AdminResult setForumModerator(int forumId, int moderatorId);
    
    // ==================== 内容管理 ====================
    
    /**
     * 获取所有主题（可按状态筛选）
     * @param status 主题状态（null表示所有状态）
     * @return 主题列表
     */
    List<Topic> getAllTopics(Topic.TopicStatus status);
    
    /**
     * 删除主题
     * @param topicId 主题ID
     * @return 操作结果
     */
    AdminResult deleteTopic(int topicId);
    
    /**
     * 批量删除主题
     * @param topicIds 主题ID数组
     * @return 操作结果
     */
    AdminResult batchDeleteTopics(int[] topicIds);
    
    /**
     * 修改主题状态
     * @param topicId 主题ID
     * @param status 新状态
     * @return 操作结果
     */
    AdminResult changeTopicStatus(int topicId, Topic.TopicStatus status);
    
    /**
     * 移动主题到其他板块
     * @param topicId 主题ID
     * @param targetForumId 目标板块ID
     * @return 操作结果
     */
    AdminResult moveTopic(int topicId, int targetForumId);
    
    /**
     * 批量移动主题
     * @param topicIds 主题ID数组
     * @param targetForumId 目标板块ID
     * @return 操作结果
     */
    AdminResult batchMoveTopics(int[] topicIds, int targetForumId);
    
    // ==================== 封禁管理 ====================
    
    /**
     * 封禁用户
     * @param userId 用户ID
     * @param reason 封禁原因
     * @param durationDays 封禁天数（0表示永久封禁）
     * @return 操作结果
     */
    AdminResult banUser(int userId, String reason, int durationDays);
    
    /**
     * 解封用户
     * @param userId 用户ID
     * @return 操作结果
     */
    AdminResult unbanUser(int userId);
    
    /**
     * 获取所有封禁记录
     * @return 封禁记录列表
     */
    List<BanRecord> getAllBanRecords();
    
    /**
     * 获取活跃的封禁记录
     * @return 活跃封禁记录列表
     */
    List<BanRecord> getActiveBanRecords();
    
    /**
     * 根据用户ID获取封禁记录
     * @param userId 用户ID
     * @return 封禁记录列表
     */
    List<BanRecord> getBanRecordsByUserId(int userId);
    
    /**
     * 删除封禁记录
     * @param banId 封禁记录ID
     * @return 操作结果
     */
    AdminResult deleteBanRecord(int banId);
    
    // ==================== 敏感词管理 ====================
    
    /**
     * 获取所有敏感词
     * @return 敏感词列表
     */
    List<SensitiveWord> getAllSensitiveWords();
    
    /**
     * 添加敏感词
     * @param word 敏感词
     * @param level 敏感等级
     * @param action 处理动作
     * @return 操作结果
     */
    AdminResult addSensitiveWord(String word, int level, String action);
    
    /**
     * 删除敏感词
     * @param wordId 敏感词ID
     * @return 操作结果
     */
    AdminResult deleteSensitiveWord(int wordId);
    
    /**
     * 批量删除敏感词
     * @param wordIds 敏感词ID数组
     * @return 操作结果
     */
    AdminResult batchDeleteSensitiveWords(int[] wordIds);
    
    /**
     * 更新敏感词
     * @param sensitiveWord 敏感词对象
     * @return 操作结果
     */
    AdminResult updateSensitiveWord(SensitiveWord sensitiveWord);
    
    // ==================== 系统统计 ====================
    
    /**
     * 获取系统统计信息
     * @return 统计信息Map
     */
    Map<String, Object> getSystemStatistics();
    
    /**
     * 获取用户统计信息
     * @return 用户统计信息
     */
    Map<String, Object> getUserStatistics();
    
    /**
     * 获取板块统计信息
     * @return 板块统计信息
     */
    Map<String, Object> getForumStatistics();
    
    /**
     * 获取内容统计信息
     * @return 内容统计信息
     */
    Map<String, Object> getContentStatistics();
    
    /**
     * 获取今日统计信息
     * @return 今日统计信息
     */
    Map<String, Object> getTodayStatistics();
    
    // ==================== 日志管理 ====================
    
    /**
     * 获取系统操作日志
     * @param page 页码
     * @param size 每页大小
     * @return 日志列表
     */
    List<Map<String, Object>> getSystemLogs(int page, int size);
    
    /**
     * 获取用户操作日志
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 日志列表
     */
    List<Map<String, Object>> getUserLogs(int userId, int page, int size);
    
    /**
     * 清除过期日志
     * @param days 保留天数
     * @return 操作结果
     */
    AdminResult clearExpiredLogs(int days);
    
    // ==================== 权限验证 ====================
    
    /**
     * 验证管理员权限
     * @param userId 用户ID
     * @return 是否有管理员权限
     */
    boolean isAdmin(int userId);
    
    /**
     * 验证用户是否可以执行管理操作
     * @param userId 用户ID
     * @param operation 操作类型
     * @return 是否可以执行
     */
    boolean canPerformAdminOperation(int userId, String operation);
    
    /**
     * 管理员操作结果类
     */
    class AdminResult {
        private boolean success;
        private String message;
        private Object data;
        
        public AdminResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public AdminResult(boolean success, String message, Object data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }
        
        // Getters and setters
        public boolean isSuccess() {
            return success;
        }
        
        public void setSuccess(boolean success) {
            this.success = success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
        
        public Object getData() {
            return data;
        }
        
        public void setData(Object data) {
            this.data = data;
        }
    }
}
