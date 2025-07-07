package main.forumsystem.src.dao;

import main.forumsystem.src.entity.BanRecord;
import main.forumsystem.src.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 封禁记录数据访问接口
 * 定义所有与用户封禁相关的数据库操作
 */
public interface BanRecordDao {
    
    /**
     * 添加封禁记录
     * @param banRecord 封禁记录对象
     * @return 是否添加成功
     */
    boolean addBanRecord(BanRecord banRecord);
    
    /**
     * 删除封禁记录
     * @param banId 封禁记录ID
     * @return 是否删除成功
     */
    boolean deleteBanRecord(int banId);
    
    /**
     * 更新封禁记录
     * @param banRecord 封禁记录对象
     * @return 是否更新成功
     */
    boolean updateBanRecord(BanRecord banRecord);
    
    /**
     * 根据封禁记录ID查询
     * @param banId 封禁记录ID
     * @return 封禁记录对象
     */
    BanRecord getBanRecordById(int banId);
    
    /**
     * 根据封禁记录ID查询（包含关联信息）
     * @param banId 封禁记录ID
     * @return 封禁记录对象（包含用户和管理员信息）
     */
    BanRecord getBanRecordWithDetails(int banId);
    
    /**
     * 根据用户ID查询所有封禁记录
     * @param userId 用户ID
     * @return 封禁记录列表
     */
    List<BanRecord> getBanRecordsByUserId(int userId);
    
    /**
     * 根据用户ID查询当前有效的封禁记录
     * @param userId 用户ID
     * @return 当前有效的封禁记录，如果没有返回null
     */
    BanRecord getCurrentBanRecord(int userId);
    
    /**
     * 根据管理员ID查询执行的封禁记录
     * @param adminId 管理员ID
     * @return 封禁记录列表
     */
    List<BanRecord> getBanRecordsByAdminId(int adminId);
    
    /**
     * 分页查询封禁记录
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @return 封禁记录列表
     */
    List<BanRecord> getBanRecordsByPage(int page, int size);
    
    /**
     * 根据状态查询封禁记录
     * @param status 封禁状态
     * @return 封禁记录列表
     */
    List<BanRecord> getBanRecordsByStatus(BanRecord.BanStatus status);
    
    /**
     * 获取所有活跃的封禁记录
     * @return 活跃封禁记录列表
     */
    List<BanRecord> getActiveBanRecords();
    
    /**
     * 获取已过期但未处理的封禁记录
     * @return 过期封禁记录列表
     */
    List<BanRecord> getExpiredBanRecords();
    
    /**
     * 解除用户封禁
     * @param userId 用户ID
     * @param adminId 操作管理员ID
     * @return 是否解除成功
     */
    boolean liftUserBan(int userId, int adminId);
    
    /**
     * 解除指定封禁记录
     * @param banId 封禁记录ID
     * @param adminId 操作管理员ID
     * @return 是否解除成功
     */
    boolean liftBanRecord(int banId, int adminId);
    
    /**
     * 检查用户是否被封禁
     * @param userId 用户ID
     * @return 是否被封禁
     */
    boolean isUserBanned(int userId);
    
    /**
     * 搜索封禁记录
     * @param keyword 关键词（用户名或封禁原因）
     * @return 封禁记录列表
     */
    List<BanRecord> searchBanRecords(String keyword);
    
    /**
     * 高级搜索封禁记录
     * @param userId 用户ID
     * @param adminId 管理员ID
     * @param status 状态
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 封禁记录列表
     */
    List<BanRecord> advancedSearchBanRecords(Integer userId, Integer adminId, 
                                           BanRecord.BanStatus status, 
                                           String startTime, String endTime);
    
    /**
     * 批量解除封禁
     * @param banIds 封禁记录ID数组
     * @param adminId 操作管理员ID
     * @return 解除成功的数量
     */
    int batchLiftBans(int[] banIds, int adminId);
    
    /**
     * 自动处理过期封禁记录
     * @return 处理的记录数量
     */
    int processExpiredBans();
    
    /**
     * 获取封禁统计信息
     * @return 统计信息Map
     */
    Map<String, Object> getBanStatistics();
    
    /**
     * 获取用户封禁历史统计
     * @param userId 用户ID
     * @return 统计信息Map
     */
    Map<String, Object> getUserBanHistory(int userId);
    
    /**
     * 获取管理员封禁操作统计
     * @param adminId 管理员ID
     * @return 统计信息Map
     */
    Map<String, Object> getAdminBanStats(int adminId);
    
    /**
     * 获取封禁记录总数
     * @return 总数
     */
    int getBanRecordCount();
    
    /**
     * 获取当前封禁用户数
     * @return 当前封禁用户数
     */
    int getCurrentBannedUserCount();
    
    /**
     * 获取今日封禁记录数
     * @return 今日封禁数
     */
    int getTodayBanCount();
    
    /**
     * 根据时间范围查询封禁记录
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 封禁记录列表
     */
    List<BanRecord> getBanRecordsByTimeRange(String startTime, String endTime);
    
    /**
     * 获取即将到期的封禁记录（未来24小时内）
     * @return 即将到期的封禁记录列表
     */
    List<BanRecord> getExpiringBanRecords();
    
    /**
     * 延长封禁时间
     * @param banId 封禁记录ID
     * @param newEndTime 新的结束时间
     * @return 是否延长成功
     */
    boolean extendBanTime(int banId, LocalDateTime newEndTime);
    
    /**
     * 修改封禁原因
     * @param banId 封禁记录ID
     * @param newReason 新的封禁原因
     * @return 是否修改成功
     */
    boolean updateBanReason(int banId, String newReason);
    
    /**
     * 获取最近的封禁记录
     * @param limit 数量限制
     * @return 最近封禁记录列表
     */
    List<BanRecord> getRecentBanRecords(int limit);
}
