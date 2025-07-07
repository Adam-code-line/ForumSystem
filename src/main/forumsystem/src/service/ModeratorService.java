package main.forumsystem.src.service;

import main.forumsystem.src.entity.Forum;
import main.forumsystem.src.entity.Topic;
import main.forumsystem.src.entity.Reply;
import main.forumsystem.src.entity.User;
import main.forumsystem.src.entity.BanRecord;
import java.util.List;

/**
 * 版主服务接口
 * 提供版主管理功能，包括板块管理、内容管理、用户管理等
 */
public interface ModeratorService {
    
    /**
     * 创建新板块（普通用户发布板块后自动成为版主）
     * @param forum 板块信息
     * @param creatorId 创建者ID
     * @return 创建结果
     */
    ModeratorResult createForum(Forum forum, int creatorId);
    
    /**
     * 更新板块信息
     * @param forum 板块信息
     * @param moderatorId 版主ID
     * @return 更新结果
     */
    ModeratorResult updateForum(Forum forum, int moderatorId);
    
    /**
     * 删除板块（只有管理员和板块创建者可以删除）
     * @param forumId 板块ID
     * @param operatorId 操作者ID
     * @return 删除结果
     */
    ModeratorResult deleteForum(int forumId, int operatorId);
    
    /**
     * 获取版主管理的板块列表
     * @param moderatorId 版主ID
     * @return 板块列表
     */
    List<Forum> getForumsByModerator(int moderatorId);
    
    /**
     * 检查用户是否是指定板块的版主
     * @param userId 用户ID
     * @param forumId 板块ID
     * @return 是否是版主
     */
    boolean isForumModerator(int userId, int forumId);
    
    /**
     * 添加板块版主
     * @param forumId 板块ID
     * @param newModeratorId 新版主ID
     * @param operatorId 操作者ID
     * @return 操作结果
     */
    ModeratorResult addForumModerator(int forumId, int newModeratorId, int operatorId);
    
    /**
     * 移除板块版主
     * @param forumId 板块ID
     * @param moderatorId 版主ID
     * @param operatorId 操作者ID
     * @return 操作结果
     */
    ModeratorResult removeForumModerator(int forumId, int moderatorId, int operatorId);
    
    /**
     * 获取板块的所有版主
     * @param forumId 板块ID
     * @return 版主列表
     */
    List<User> getForumModerators(int forumId);
    
    /**
     * 置顶主题
     * @param topicId 主题ID
     * @param moderatorId 版主ID
     * @return 操作结果
     */
    ModeratorResult pinTopic(int topicId, int moderatorId);
    
    /**
     * 取消置顶主题
     * @param topicId 主题ID
     * @param moderatorId 版主ID
     * @return 操作结果
     */
    ModeratorResult unpinTopic(int topicId, int moderatorId);
    
    /**
     * 加精主题
     * @param topicId 主题ID
     * @param moderatorId 版主ID
     * @return 操作结果
     */
    ModeratorResult highlightTopic(int topicId, int moderatorId);
    
    /**
     * 取消加精主题
     * @param topicId 主题ID
     * @param moderatorId 版主ID
     * @return 操作结果
     */
    ModeratorResult unhighlightTopic(int topicId, int moderatorId);
    
    /**
     * 锁定主题
     * @param topicId 主题ID
     * @param moderatorId 版主ID
     * @param reason 锁定原因
     * @return 操作结果
     */
    ModeratorResult lockTopic(int topicId, int moderatorId, String reason);
    
    /**
     * 解锁主题
     * @param topicId 主题ID
     * @param moderatorId 版主ID
     * @return 操作结果
     */
    ModeratorResult unlockTopic(int topicId, int moderatorId);
    
    /**
     * 删除主题
     * @param topicId 主题ID
     * @param moderatorId 版主ID
     * @param reason 删除原因
     * @return 操作结果
     */
    ModeratorResult deleteTopic(int topicId, int moderatorId, String reason);
    
    /**
     * 移动主题到其他板块
     * @param topicId 主题ID
     * @param targetForumId 目标板块ID
     * @param moderatorId 版主ID
     * @return 操作结果
     */
    ModeratorResult moveTopic(int topicId, int targetForumId, int moderatorId);
    
    /**
     * 删除回复
     * @param replyId 回复ID
     * @param moderatorId 版主ID
     * @param reason 删除原因
     * @return 操作结果
     */
    ModeratorResult deleteReply(int replyId, int moderatorId, String reason);
    
    /**
     * 批量删除回复
     * @param replyIds 回复ID数组
     * @param moderatorId 版主ID
     * @param reason 删除原因
     * @return 操作结果
     */
    ModeratorResult batchDeleteReplies(int[] replyIds, int moderatorId, String reason);
    
    /**
     * 临时封禁用户（仅在版主管理的板块内）
     * @param userId 用户ID
     * @param forumId 板块ID
     * @param moderatorId 版主ID
     * @param reason 封禁原因
     * @param durationDays 封禁天数
     * @return 操作结果
     */
    ModeratorResult banUserFromForum(int userId, int forumId, int moderatorId, String reason, int durationDays);
    
    /**
     * 解封用户（仅在版主管理的板块内）
     * @param userId 用户ID
     * @param forumId 板块ID
     * @param moderatorId 版主ID
     * @return 操作结果
     */
    ModeratorResult unbanUserFromForum(int userId, int forumId, int moderatorId);
    
    /**
     * 获取板块封禁记录
     * @param forumId 板块ID
     * @param moderatorId 版主ID
     * @return 封禁记录列表
     */
    List<BanRecord> getForumBanRecords(int forumId, int moderatorId);
    
    /**
     * 检查用户是否在指定板块被封禁
     * @param userId 用户ID
     * @param forumId 板块ID
     * @return 是否被封禁
     */
    boolean isUserBannedFromForum(int userId, int forumId);
    
    /**
     * 获取板块管理统计信息
     * @param forumId 板块ID
     * @param moderatorId 版主ID
     * @return 统计信息
     */
    ForumStatistics getForumStatistics(int forumId, int moderatorId);
    
    /**
     * 获取需要审核的内容列表
     * @param forumId 板块ID
     * @param moderatorId 版主ID
     * @return 待审核内容列表
     */
    List<Topic> getPendingTopics(int forumId, int moderatorId);
    
    /**
     * 审核主题
     * @param topicId 主题ID
     * @param moderatorId 版主ID
     * @param approved 是否通过审核
     * @param reason 审核意见
     * @return 操作结果
     */
    ModeratorResult reviewTopic(int topicId, int moderatorId, boolean approved, String reason);
    
    /**
     * 版主操作结果类
     */
    class ModeratorResult {
        private boolean success;
        private String message;
        private Object data;
        
        public ModeratorResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public ModeratorResult(boolean success, String message, Object data) {
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
        private int totalTopics;
        private int totalReplies;
        private int totalUsers;
        private int todayTopics;
        private int todayReplies;
        private int pendingTopics;
        private int bannedUsers;
        
        public ForumStatistics(int totalTopics, int totalReplies, int totalUsers, 
                             int todayTopics, int todayReplies, int pendingTopics, int bannedUsers) {
            this.totalTopics = totalTopics;
            this.totalReplies = totalReplies;
            this.totalUsers = totalUsers;
            this.todayTopics = todayTopics;
            this.todayReplies = todayReplies;
            this.pendingTopics = pendingTopics;
            this.bannedUsers = bannedUsers;
        }
        
        // Getters and Setters
        public int getTotalTopics() { return totalTopics; }
        public void setTotalTopics(int totalTopics) { this.totalTopics = totalTopics; }
        
        public int getTotalReplies() { return totalReplies; }
        public void setTotalReplies(int totalReplies) { this.totalReplies = totalReplies; }
        
        public int getTotalUsers() { return totalUsers; }
        public void setTotalUsers(int totalUsers) { this.totalUsers = totalUsers; }
        
        public int getTodayTopics() { return todayTopics; }
        public void setTodayTopics(int todayTopics) { this.todayTopics = todayTopics; }
        
        public int getTodayReplies() { return todayReplies; }
        public void setTodayReplies(int todayReplies) { this.todayReplies = todayReplies; }
        
        public int getPendingTopics() { return pendingTopics; }
        public void setPendingTopics(int pendingTopics) { this.pendingTopics = pendingTopics; }
        
        public int getBannedUsers() { return bannedUsers; }
        public void setBannedUsers(int bannedUsers) { this.bannedUsers = bannedUsers; }
    }
}
