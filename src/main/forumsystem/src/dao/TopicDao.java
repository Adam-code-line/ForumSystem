package main.forumsystem.src.dao;

import main.forumsystem.src.entity.Topic;
import main.forumsystem.src.entity.User;
import java.util.List;
import java.util.Map;

/**
 * 主题帖数据访问接口
 * 定义所有与主题帖相关的数据库操作
 */
public interface TopicDao {
    
    /**
     * 发布新主题
     * @param topic 主题对象
     * @return 是否发布成功
     */
    boolean addTopic(Topic topic);
    
    /**
     * 根据主题ID删除主题
     * @param topicId 主题ID
     * @return 是否删除成功
     */
    boolean deleteTopic(int topicId);
    
    /**
     * 更新主题信息
     * @param topic 主题对象
     * @return 是否更新成功
     */
    boolean updateTopic(Topic topic);
    
    /**
     * 根据主题ID查询主题
     * @param topicId 主题ID
     * @return 主题对象，如果不存在返回null
     */
    Topic getTopicById(int topicId);
    
    /**
     * 根据主题ID查询主题（包含关联信息）
     * @param topicId 主题ID
     * @return 主题对象（包含作者、版块、最后回复者信息）
     */
    Topic getTopicWithDetails(int topicId);
    
    /**
     * 根据版块ID查询主题列表
     * @param forumId 版块ID
     * @return 主题列表
     */
    List<Topic> getTopicsByForumId(int forumId);
    
    /**
     * 根据用户ID查询用户发布的主题
     * @param userId 用户ID
     * @return 主题列表
     */
    List<Topic> getTopicsByUserId(int userId);
    
    /**
     * 分页查询版块的主题列表
     * @param forumId 版块ID
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @param orderBy 排序方式（create_time, last_reply_time, view_count等）
     * @param isDesc 是否降序
     * @return 主题列表
     */
    List<Topic> getTopicsByPage(int forumId, int page, int size, String orderBy, boolean isDesc);
    
    /**
     * 获取置顶主题列表
     * @param forumId 版块ID（0表示全站置顶）
     * @return 置顶主题列表
     */
    List<Topic> getPinnedTopics(int forumId);
    
    /**
     * 搜索主题（按标题和内容）
     * @param keyword 关键词
     * @param forumId 版块ID（0表示全站搜索）
     * @return 主题列表
     */
    List<Topic> searchTopics(String keyword, int forumId);
    
    /**
     * 高级搜索主题
     * @param keyword 关键词
     * @param forumId 版块ID
     * @param userId 作者ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 主题列表
     */
    List<Topic> advancedSearchTopics(String keyword, int forumId, int userId, 
                                   String startTime, String endTime);
    
    /**
     * 增加主题浏览次数
     * @param topicId 主题ID
     * @return 是否更新成功
     */
    boolean incrementViewCount(int topicId);
    
    /**
     * 更新主题回复计数
     * @param topicId 主题ID
     * @param increment 增减数量（正数增加，负数减少）
     * @return 是否更新成功
     */
    boolean updateReplyCount(int topicId, int increment);
    
    /**
     * 更新主题最后回复信息
     * @param topicId 主题ID
     * @param lastReplyUserId 最后回复用户ID
     * @return 是否更新成功
     */
    boolean updateLastReplyInfo(int topicId, int lastReplyUserId);
    
    /**
     * 置顶/取消置顶主题
     * @param topicId 主题ID
     * @param isPinned 是否置顶
     * @return 是否操作成功
     */
    boolean pinTopic(int topicId, boolean isPinned);
    
    /**
     * 锁定/解锁主题
     * @param topicId 主题ID
     * @param isLocked 是否锁定
     * @return 是否操作成功
     */
    boolean lockTopic(int topicId, boolean isLocked);
    
    /**
     * 修改主题状态
     * @param topicId 主题ID
     * @param status 新状态
     * @return 是否修改成功
     */
    boolean changeTopicStatus(int topicId, Topic.TopicStatus status);
    
    /**
     * 移动主题到其他版块
     * @param topicId 主题ID
     * @param newForumId 新版块ID
     * @return 是否移动成功
     */
    boolean moveTopic(int topicId, int newForumId);
    
    /**
     * 获取热门主题（按浏览量排序）
     * @param limit 数量限制
     * @param days 统计天数（0表示全部时间）
     * @return 热门主题列表
     */
    List<Topic> getHotTopics(int limit, int days);
    
    /**
     * 获取最新主题
     * @param limit 数量限制
     * @return 最新主题列表
     */
    List<Topic> getLatestTopics(int limit);
    
    /**
     * 获取精华主题（高回复量主题）
     * @param limit 数量限制
     * @param minReplies 最少回复数
     * @return 精华主题列表
     */
    List<Topic> getFeaturedTopics(int limit, int minReplies);
    
    /**
     * 获取版块主题总数
     * @param forumId 版块ID
     * @return 主题总数
     */
    int getTopicCount(int forumId);
    
    /**
     * 获取用户发布的主题总数
     * @param userId 用户ID
     * @return 主题总数
     */
    int getUserTopicCount(int userId);
    
    /**
     * 获取今日发布的主题数
     * @param forumId 版块ID（0表示全站）
     * @return 今日主题数
     */
    int getTodayTopicCount(int forumId);
    
    /**
     * 批量删除主题
     * @param topicIds 主题ID数组
     * @return 删除成功的主题数量
     */
    int batchDeleteTopics(int[] topicIds);
    
    /**
     * 批量移动主题
     * @param topicIds 主题ID数组
     * @param newForumId 新版块ID
     * @return 移动成功的主题数量
     */
    int batchMoveTopics(int[] topicIds, int newForumId);
    
    /**
     * 获取主题统计信息
     * @param forumId 版块ID（0表示全站）
     * @return 统计信息Map（包含总数、今日数、本周数等）
     */
    Map<String, Object> getTopicStatistics(int forumId);
    
    /**
     * 获取用户在指定版块的主题列表
     * @param userId 用户ID
     * @param forumId 版块ID
     * @param page 页码
     * @param size 每页大小
     * @return 主题列表
     */
    List<Topic> getUserTopicsInForum(int userId, int forumId, int page, int size);
    
    /**
     * 检查用户是否可以在版块发帖
     * @param userId 用户ID
     * @param forumId 版块ID
     * @return 是否可以发帖
     */
    boolean canUserPostInForum(int userId, int forumId);
    
    /**
     * 获取相关主题（基于标题相似度）
     * @param topicId 当前主题ID
     * @param limit 数量限制
     * @return 相关主题列表
     */
    List<Topic> getRelatedTopics(int topicId, int limit);
}
