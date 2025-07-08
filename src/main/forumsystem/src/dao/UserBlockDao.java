package main.forumsystem.src.dao;

import main.forumsystem.src.entity.UserBlock;
import java.util.List;

/**
 * 用户拉黑数据访问接口
 */
public interface UserBlockDao {
    
    /**
     * 添加拉黑记录
     */
    boolean addBlock(UserBlock userBlock);
    
    /**
     * 移除拉黑（取消拉黑）
     */
    boolean removeBlock(int blockerId, int blockedId);
    
    /**
     * 检查用户A是否拉黑了用户B
     */
    boolean isBlocked(int blockerId, int blockedId);
    
    /**
     * 获取用户的拉黑列表
     */
    List<UserBlock> getUserBlockList(int userId);
    
    /**
     * 获取拉黑了指定用户的用户列表
     */
    List<UserBlock> getBlockedByList(int blockedUserId);
    
    /**
     * 获取用户的拉黑记录
     */
    UserBlock getBlockRecord(int blockerId, int blockedId);
    
    /**
     * 清理已删除用户的拉黑记录
     */
    boolean cleanupBlocksForDeletedUser(int userId);
}
