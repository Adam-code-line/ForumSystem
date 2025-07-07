package main.forumsystem.src.dao;

import main.forumsystem.src.entity.Forum;
import main.forumsystem.src.entity.User;
import java.util.List;
import java.util.Map;

/**
 * 版块数据访问接口
 * 定义所有与版块相关的数据库操作
 */
public interface ForumDao {
    
    /**
     * 添加新版块
     * @param forum 版块对象
     * @return 是否添加成功
     */
    boolean addForum(Forum forum);
    
    /**
     * 删除版块
     * @param forumId 版块ID
     * @return 是否删除成功
     */
    boolean deleteForum(int forumId);
    
    /**
     * 更新版块信息
     * @param forum 版块对象
     * @return 是否更新成功
     */
    boolean updateForum(Forum forum);
    
    /**
     * 根据版块ID查询版块
     * @param forumId 版块ID
     * @return 版块对象
     */
    Forum getForumById(int forumId);
    
    /**
     * 根据版块ID查询版块（包含版主信息）
     * @param forumId 版块ID
     * @return 版块对象（包含版主详细信息）
     */
    Forum getForumWithModerator(int forumId);
    
    /**
     * 根据版块名称查询版块
     * @param forumName 版块名称
     * @return 版块对象
     */
    Forum getForumByName(String forumName);
    
    /**
     * 获取所有版块列表
     * @return 版块列表（按排序字段排序）
     */
    List<Forum> getAllForums();
    
    /**
     * 获取活跃状态的版块列表
     * @return 活跃版块列表
     */
    List<Forum> getActiveForums();
    
    /**
     * 分页查询版块
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @return 版块列表
     */
    List<Forum> getForumsByPage(int page, int size);
    
    /**
     * 根据版主ID查询管理的版块
     * @param moderatorId 版主ID
     * @return 版块列表
     */
    List<Forum> getForumsByModerator(int moderatorId);
    
    /**
     * 根据状态查询版块
     * @param status 版块状态
     * @return 版块列表
     */
    List<Forum> getForumsByStatus(Forum.ForumStatus status);
    
    /**
     * 搜索版块（按名称和描述）
     * @param keyword 关键词
     * @return 版块列表
     */
    List<Forum> searchForums(String keyword);
    
    /**
     * 修改版块状态
     * @param forumId 版块ID
     * @param status 新状态
     * @return 是否修改成功
     */
    boolean changeForumStatus(int forumId, Forum.ForumStatus status);
    
    /**
     * 设置板块版主
     * @param forumId 板块ID
     * @param moderatorId 版主ID
     * @return 是否设置成功
     */
    boolean setForumModerator(int forumId, int moderatorId);

    /**
     * 更新板块主题数量
     * @param forumId 板块ID
     * @param increment 增量（可为负数）
     * @return 是否更新成功
     */
    boolean updateTopicCount(int forumId, int increment);
    
    /**
     * 更新版块帖子计数
     * @param forumId 版块ID
     * @param increment 增减数量（正数增加，负数减少）
     * @return 是否更新成功
     */
    boolean updatePostCount(int forumId, int increment);
    
    /**
     * 重新计算版块统计数据
     * @param forumId 版块ID
     * @return 是否重新计算成功
     */
    boolean recalculateForumStats(int forumId);
    
    /**
     * 调整版块排序
     * @param forumId 版块ID
     * @param sortOrder 新的排序值
     * @return 是否调整成功
     */
    boolean updateSortOrder(int forumId, int sortOrder);
    
    /**
     * 批量调整版块排序
     * @param forumIds 版块ID数组
     * @param sortOrders 对应的排序值数组
     * @return 调整成功的数量
     */
    int batchUpdateSortOrder(int[] forumIds, int[] sortOrders);
    
    /**
     * 获取版块总数
     * @return 版块总数
     */
    int getForumCount();
    
    /**
     * 获取活跃版块数
     * @return 活跃版块数
     */
    int getActiveForumCount();
    
    /**
     * 检查版块名称是否存在
     * @param forumName 版块名称
     * @param excludeId 排除的版块ID（用于更新时检查）
     * @return 是否存在
     */
    boolean forumNameExists(String forumName, int excludeId);
    
    /**
     * 获取版块统计信息
     * @param forumId 版块ID（0表示全站统计）
     * @return 统计信息Map
     */
    Map<String, Object> getForumStatistics(int forumId);
    
    /**
     * 获取最热门的版块（按帖子数排序）
     * @param limit 数量限制
     * @return 热门版块列表
     */
    List<Forum> getHotForums(int limit);
    
    /**
     * 获取最新的版块
     * @param limit 数量限制
     * @return 最新版块列表
     */
    List<Forum> getLatestForums(int limit);
    
    /**
     * 检查用户是否为版块版主
     * @param userId 用户ID
     * @param forumId 版块ID
     * @return 是否为版主
     */
    boolean isUserModerator(int userId, int forumId);
    
    /**
     * 获取版块的最新主题信息
     * @param forumId 版块ID
     * @param limit 数量限制
     * @return 主题信息列表
     */
    List<Map<String, Object>> getLatestTopicsInfo(int forumId, int limit);
    
    /**
     * 批量删除版块
     * @param forumIds 版块ID数组
     * @return 删除成功的数量
     */
    int batchDeleteForums(int[] forumIds);
    
    /**
     * 获取版块层级结构（如果支持父子版块）
     * @param parentId 父版块ID（0表示顶级版块）
     * @return 版块列表
     */
    List<Forum> getForumsByParent(int parentId);
}
