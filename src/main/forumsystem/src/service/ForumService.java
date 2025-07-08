package main.forumsystem.src.service;

import main.forumsystem.src.entity.User;
import main.forumsystem.src.entity.Forum;
import main.forumsystem.src.entity.Topic;
import main.forumsystem.src.entity.Reply;

import java.util.List;

/**
 * 板块服务接口
 * 专注于论坛内容管理的核心业务逻辑，不包含权限验证
 */
public interface ForumService {
    
    // ==================== 板块管理 ====================
    
    /**
     * 创建板块（纯业务逻辑）
     */
    ForumResult createForum(int userId, String forumName, String description);
    
    /**
     * 获取板块信息
     */
    Forum getForumById(int forumId);
    
    /**
     * 获取所有板块
     */
    List<Forum> getAllForums();
    
    /**
     * 更新板块信息
     */
    ForumResult updateForum(int forumId, String forumName, String description);
    
    // ==================== 主题管理 ====================
    
    /**
     * 创建主题（纯业务逻辑）
     */
    ForumResult createTopic(int userId, String title, String content, int forumId);
    
    /**
     * 获取主题信息
     */
    Topic getTopicById(int topicId);
    
    /**
     * 获取板块下的主题列表
     */
    List<Topic> getTopicsByForum(int forumId, int page, int size);
    
    /**
     * 更新主题信息
     */
    ForumResult updateTopic(int topicId, String title, String content);
    
    /**
     * 置顶主题
     */
    boolean pinTopic(int topicId, boolean isPinned);
    
    /**
     * 锁定主题
     */
    boolean lockTopic(int topicId, boolean isLocked);
    
    /**
     * 增加主题浏览量
     */
    boolean increaseTopicViewCount(int topicId);
    
    // ==================== 回复管理 ====================
    
    /**
     * 创建回复（纯业务逻辑）
     */
    ForumResult createReply(int userId, String content, int topicId);
    
    /**
     * 获取回复信息
     */
    Reply getReplyById(int replyId);
    
    /**
     * 获取主题下的回复列表
     */
    List<Reply> getRepliesByTopic(int topicId, int page, int size);
    
    /**
     * 更新回复内容
     */
    ForumResult updateReply(int replyId, String content);
    
    // ==================== 搜索功能 ====================
    
    /**
     * 搜索主题
     */
    List<Topic> searchTopics(String keyword, int page, int size);
    
    /**
     * 搜索板块
     */
    List<Forum> searchForums(String keyword);
    
    // ==================== 统计信息 ====================
    
    /**
     * 获取板块统计信息
     */
    ForumStatistics getForumStatistics(int forumId);
    
    /**
     * 获取用户发帖统计
     */
    UserPostStatistics getUserPostStatistics(int userId);
    
    // ==================== 权限检查（使用工厂模式） ====================
    
    /**
     * 检查用户是否可以执行某个操作
     */
    boolean canUserPerformAction(int userId, String action, int targetId);
    
    // ==================== 结果类 ====================
    
    /**
     * 操作结果类
     */
    class ForumResult {
        private boolean success;
        private String message;
        private Object data;
        
        public ForumResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public ForumResult(boolean success, String message, Object data) {
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
    
    /**
     * 板块统计信息类
     */
    class ForumStatistics {
        private int topicCount;
        private int postCount;
        private int todayTopicCount;
        private int todayPostCount;
        
        public ForumStatistics(int topicCount, int postCount, int todayTopicCount, int todayPostCount) {
            this.topicCount = topicCount;
            this.postCount = postCount;
            this.todayTopicCount = todayTopicCount;
            this.todayPostCount = todayPostCount;
        }
        
        // Getters
        public int getTopicCount() { return topicCount; }
        public int getPostCount() { return postCount; }
        public int getTodayTopicCount() { return todayTopicCount; }
        public int getTodayPostCount() { return todayPostCount; }
    }
    
    /**
     * 用户发帖统计类
     */
    class UserPostStatistics {
        private int totalTopics;
        private int totalReplies;
        private int todayTopics;
        private int todayReplies;
        
        public UserPostStatistics(int totalTopics, int totalReplies, int todayTopics, int todayReplies) {
            this.totalTopics = totalTopics;
            this.totalReplies = totalReplies;
            this.todayTopics = todayTopics;
            this.todayReplies = todayReplies;
        }
        
        // Getters
        public int getTotalTopics() { return totalTopics; }
        public int getTotalReplies() { return totalReplies; }
        public int getTodayTopics() { return todayTopics; }
        public int getTodayReplies() { return todayReplies; }
    }
}
