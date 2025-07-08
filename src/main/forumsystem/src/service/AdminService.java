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
 * 专注于系统级管理功能的业务逻辑，不包含权限验证
 */
public interface AdminService {
    
    // ==================== 系统统计信息 ====================
    
    /**
     * 获取系统统计信息
     */
    Map<String, Object> getSystemStatistics();
    
    /**
     * 获取用户统计信息
     */
    Map<String, Object> getUserStatistics();
    
    /**
     * 获取论坛统计信息
     */
    Map<String, Object> getForumStatistics();
    
    /**
     * 获取内容统计信息
     */
    Map<String, Object> getContentStatistics();
    
    // ==================== 内容管理 ====================
    
    /**
     * 删除论坛板块
     */
    AdminResult deleteForum(int forumId);
    
    /**
     * 删除主题
     */
    AdminResult deleteTopic(int topicId);
    
    /**
     * 删除回复
     */
    AdminResult deleteReply(int replyId);
    
    /**
     * 批量删除主题
     */
    int batchDeleteTopics(int[] topicIds);
    
    /**
     * 批量删除回复
     */
    int batchDeleteReplies(int[] replyIds);
    
    // ==================== 敏感词管理 ====================
    
    /**
     * 添加敏感词
     */
    boolean addSensitiveWord(String word);
    
    /**
     * 删除敏感词
     */
    boolean deleteSensitiveWord(int wordId);
    
    /**
     * 获取所有敏感词
     */
    List<SensitiveWord> getAllSensitiveWords();
    
    /**
     * 检查文本是否包含敏感词
     */
    boolean containsSensitiveWord(String text);
    
    // ==================== 封禁记录管理 ====================
    
    /**
     * 创建封禁记录
     */
    boolean createBanRecord(int userId, int adminId, String reason, long durationHours);
    
    /**
     * 获取用户封禁记录
     */
    List<BanRecord> getUserBanRecords(int userId);
    
    /**
     * 获取所有封禁记录
     */
    List<BanRecord> getAllBanRecords();
    
    // ==================== 系统设置 ====================
    
    /**
     * 更新系统设置
     */
    boolean updateSystemSetting(String key, String value);
    
    /**
     * 获取系统设置
     */
    String getSystemSetting(String key);
    
    /**
     * 获取所有系统设置
     */
    Map<String, String> getAllSystemSettings();
    
    // ==================== 数据清理 ====================
    
    /**
     * 清理过期数据
     */
    AdminResult cleanExpiredData();
    
    /**
     * 清理无效用户
     */
    AdminResult cleanInactiveUsers(int inactiveDays);
    
    /**
     * 清理空板块
     */
    AdminResult cleanEmptyForums();
    
    // ==================== 结果类 ====================
    
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
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public Object getData() {
            return data;
        }
    }
}
