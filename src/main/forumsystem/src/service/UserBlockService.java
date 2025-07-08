package main.forumsystem.src.service;

import main.forumsystem.src.entity.UserBlock;
import main.forumsystem.src.entity.User;
import java.util.List;

/**
 * 用户拉黑服务接口
 */
public interface UserBlockService {
    
    /**
     * 拉黑用户
     */
    BlockResult blockUser(int blockerId, int blockedId, String reason);
    
    /**
     * 取消拉黑
     */
    BlockResult unblockUser(int blockerId, int blockedId);
    
    /**
     * 检查用户A是否拉黑了用户B
     */
    boolean isUserBlocked(int blockerId, int blockedId);
    
    /**
     * 检查用户是否可以在指定板块发言（考虑拉黑关系）
     */
    boolean canUserPostInForum(int userId, int forumId);
    
    /**
     * 检查用户是否可以在指定主题下回复（考虑拉黑关系）
     */
    boolean canUserReplyToTopic(int userId, int topicId);
    
    /**
     * 获取用户的拉黑列表
     */
    List<User> getBlockedUsers(int userId);
    
    /**
     * 获取拉黑了指定用户的用户列表
     */
    List<User> getBlockedByUsers(int userId);
    
    /**
     * 过滤内容中被拉黑用户的发言
     */
    <T> List<T> filterBlockedContent(int viewerId, List<T> contentList);
    
    /**
     * 检查是否存在相互拉黑
     */
    boolean isMutualBlock(int userId1, int userId2);
    
    /**
     * 拉黑操作结果类
     */
    class BlockResult {
        private boolean success;
        private String message;
        private Object data;
        
        public BlockResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public BlockResult(boolean success, String message, Object data) {
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
