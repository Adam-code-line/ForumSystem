package main.forumsystem.src.dao;

import main.forumsystem.src.entity.Reply;
import main.forumsystem.src.entity.User;
import java.util.List;
import java.util.Map;

/**
 * 回复数据访问接口
 * 定义所有与回复相关的数据库操作
 */
public interface ReplyDao {
    
    /**
     * 添加回复
     * @param reply 回复对象
     * @return 是否添加成功
     */
    boolean addReply(Reply reply);
    
    /**
     * 删除回复（软删除）
     * @param replyId 回复ID
     * @return 是否删除成功
     */
    boolean deleteReply(int replyId);
    
    /**
     * 物理删除回复
     * @param replyId 回复ID
     * @return 是否删除成功
     */
    boolean hardDeleteReply(int replyId);
    
    /**
     * 更新回复内容
     * @param reply 回复对象
     * @return 是否更新成功
     */
    boolean updateReply(Reply reply);
    
    /**
     * 根据回复ID查询回复
     * @param replyId 回复ID
     * @return 回复对象
     */
    Reply getReplyById(int replyId);
    
    /**
     * 根据回复ID查询回复（包含关联信息）
     * @param replyId 回复ID
     * @return 回复对象（包含作者、主题、被回复对象信息）
     */
    Reply getReplyWithDetails(int replyId);
    
    /**
     * 根据主题ID查询所有回复
     * @param topicId 主题ID
     * @return 回复列表
     */
    List<Reply> getRepliesByTopicId(int topicId);
    
    /**
     * 根据主题ID分页查询回复
     * @param topicId 主题ID
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @return 回复列表
     */
    List<Reply> getRepliesByTopicId(int topicId, int page, int size);
    
    /**
     * 根据用户ID查询用户的所有回复
     * @param userId 用户ID
     * @return 回复列表
     */
    List<Reply> getRepliesByUserId(int userId);
    
    /**
     * 根据用户ID分页查询用户的回复
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 回复列表
     */
    List<Reply> getRepliesByUserId(int userId, int page, int size);
    
    /**
     * 获取某条回复的所有子回复（嵌套回复）
     * @param replyToId 被回复的回复ID
     * @return 子回复列表
     */
    List<Reply> getChildReplies(int replyToId);
    
    /**
     * 获取主题的回复树形结构
     * @param topicId 主题ID
     * @return 按层级排序的回复列表
     */
    List<Reply> getReplyTreeByTopicId(int topicId);
    
    /**
     * 修改回复状态
     * @param replyId 回复ID
     * @param status 新状态
     * @return 是否修改成功
     */
    boolean changeReplyStatus(int replyId, Reply.ReplyStatus status);
    
    /**
     * 批量删除回复
     * @param replyIds 回复ID数组
     * @return 删除成功的回复数量
     */
    int batchDeleteReplies(int[] replyIds);
    
    /**
     * 批量修改回复状态
     * @param replyIds 回复ID数组
     * @param status 新状态
     * @return 修改成功的回复数量
     */
    int batchChangeReplyStatus(int[] replyIds, Reply.ReplyStatus status);
    
    /**
     * 根据内容搜索回复
     * @param keyword 关键词
     * @param topicId 主题ID（0表示全局搜索）
     * @return 回复列表
     */
    List<Reply> searchReplies(String keyword, int topicId);
    
    /**
     * 高级搜索回复
     * @param keyword 关键词
     * @param topicId 主题ID
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 回复列表
     */
    List<Reply> advancedSearchReplies(String keyword, int topicId, int userId, 
                                    String startTime, String endTime);
    
    /**
     * 获取主题的回复总数
     * @param topicId 主题ID
     * @return 回复总数
     */
    int getReplyCount(int topicId);
    
    /**
     * 获取用户的回复总数
     * @param userId 用户ID
     * @return 回复总数
     */
    int getUserReplyCount(int userId);
    
    /**
     * 获取今日回复数
     * @param topicId 主题ID（0表示全站）
     * @return 今日回复数
     */
    int getTodayReplyCount(int topicId);
    
    /**
     * 获取最新回复列表
     * @param limit 数量限制
     * @return 最新回复列表
     */
    List<Reply> getLatestReplies(int limit);
    
    /**
     * 获取用户在指定主题的回复
     * @param userId 用户ID
     * @param topicId 主题ID
     * @return 回复列表
     */
    List<Reply> getUserRepliesInTopic(int userId, int topicId);
    
    /**
     * 获取回复统计信息
     * @param topicId 主题ID（0表示全站）
     * @return 统计信息Map
     */
    Map<String, Object> getReplyStatistics(int topicId);
    
    /**
     * 检查用户是否可以回复主题
     * @param userId 用户ID
     * @param topicId 主题ID
     * @return 是否可以回复
     */
    boolean canUserReplyToTopic(int userId, int topicId);
    
    /**
     * 获取某个时间段内的回复列表
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 数量限制
     * @return 回复列表
     */
    List<Reply> getRepliesByTimeRange(String startTime, String endTime, int limit);
    
    /**
     * 删除主题的所有回复
     * @param topicId 主题ID
     * @return 删除的回复数量
     */
    int deleteAllRepliesByTopicId(int topicId);
    
    /**
     * 获取回复的层级深度
     * @param replyId 回复ID
     * @return 层级深度（0表示顶级回复）
     */
    int getReplyDepth(int replyId);
    
    /**
     * 获取热门回复（按子回复数量排序）
     * @param topicId 主题ID
     * @param limit 数量限制
     * @return 热门回复列表
     */
    List<Reply> getHotReplies(int topicId, int limit);

    /**
     * 获取所有回复
     * @return 所有回复列表
     */
    List<Reply> getAllReplies();
}
