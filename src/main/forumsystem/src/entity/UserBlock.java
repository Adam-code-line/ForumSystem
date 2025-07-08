package main.forumsystem.src.entity;

import java.time.LocalDateTime;

/**
 * 用户拉黑关系实体类
 * 对应数据库表：user_blocks
 */
public class UserBlock {
    private int blockId;
    private int blockerId;    // 拉黑者ID
    private int blockedId;    // 被拉黑者ID
    private LocalDateTime blockTime;
    private BlockStatus status;
    private String reason;    // 拉黑原因（可选）
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * 拉黑状态枚举
     */
    public enum BlockStatus {
        ACTIVE("active"),
        REMOVED("removed");
        
        private final String value;
        
        BlockStatus(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static BlockStatus fromValue(String value) {
            for (BlockStatus status : BlockStatus.values()) {
                if (status.value.equals(value)) {
                    return status;
                }
            }
            return ACTIVE;
        }
    }
    
    // 构造方法
    public UserBlock() {}
    
    public UserBlock(int blockerId, int blockedId, String reason) {
        this.blockerId = blockerId;
        this.blockedId = blockedId;
        this.reason = reason;
        this.blockTime = LocalDateTime.now();
        this.status = BlockStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public int getBlockId() {
        return blockId;
    }
    
    public void setBlockId(int blockId) {
        this.blockId = blockId;
    }
    
    public int getBlockerId() {
        return blockerId;
    }
    
    public void setBlockerId(int blockerId) {
        this.blockerId = blockerId;
    }
    
    public int getBlockedId() {
        return blockedId;
    }
    
    public void setBlockedId(int blockedId) {
        this.blockedId = blockedId;
    }
    
    public LocalDateTime getBlockTime() {
        return blockTime;
    }
    
    public void setBlockTime(LocalDateTime blockTime) {
        this.blockTime = blockTime;
    }
    
    public BlockStatus getStatus() {
        return status;
    }
    
    public void setStatus(BlockStatus status) {
        this.status = status;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "UserBlock{" +
                "blockId=" + blockId +
                ", blockerId=" + blockerId +
                ", blockedId=" + blockedId +
                ", blockTime=" + blockTime +
                ", status=" + status +
                ", reason='" + reason + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
