package main.forumsystem.src.entity;

import java.time.LocalDateTime;

/**
 * 版块实体类
 * 对应数据库表：forums
 */
public class Forum {
    private int forumId;
    private String forumName;
    private String description;
    private int moderatorId;
    private int topicCount;
    private int postCount;
    private ForumStatus status;
    private LocalDateTime createTime;
    private int sortOrder;

    // 关联对象
    private User moderator;

    /**
     * 版块状态枚举
     */
    public enum ForumStatus {
        ACTIVE("active"),
        LOCKED("locked");

        private final String value;

        ForumStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static ForumStatus fromValue(String value) {
            for (ForumStatus status : ForumStatus.values()) {
                if (status.value.equals(value)) {
                    return status;
                }
            }
            return ACTIVE; // 默认返回活跃状态
        }
    }

    // 构造函数
    public Forum() {}

    public Forum(String forumName, String description) {
        this.forumName = forumName;
        this.description = description;
        this.status = ForumStatus.ACTIVE;
        this.topicCount = 0;
        this.postCount = 0;
        this.sortOrder = 0;
    }

    // Getters and Setters
    public int getForumId() {
        return forumId;
    }

    public void setForumId(int forumId) {
        this.forumId = forumId;
    }

    public String getForumName() {
        return forumName;
    }

    public void setForumName(String forumName) {
        this.forumName = forumName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getModeratorId() {
        return moderatorId;
    }

    public void setModeratorId(int moderatorId) {
        this.moderatorId = moderatorId;
    }

    public int getTopicCount() {
        return topicCount;
    }

    public void setTopicCount(int topicCount) {
        this.topicCount = topicCount;
    }

    public int getPostCount() {
        return postCount;
    }

    public void setPostCount(int postCount) {
        this.postCount = postCount;
    }

    public ForumStatus getStatus() {
        return status;
    }

    public void setStatus(ForumStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public User getModerator() {
        return moderator;
    }

    public void setModerator(User moderator) {
        this.moderator = moderator;
    }

    // 便民方法
    public boolean isLocked() {
        return status == ForumStatus.LOCKED;
    }

    public boolean isActive() {
        return status == ForumStatus.ACTIVE;
    }

    public void incrementTopicCount() {
        this.topicCount++;
    }

    public void decrementTopicCount() {
        if (this.topicCount > 0) {
            this.topicCount--;
        }
    }

    public void incrementPostCount() {
        this.postCount++;
    }

    public void decrementPostCount() {
        if (this.postCount > 0) {
            this.postCount--;
        }
    }

    @Override
    public String toString() {
        return "Forum{" +
                "forumId=" + forumId +
                ", forumName='" + forumName + '\'' +
                ", description='" + description + '\'' +
                ", moderatorId=" + moderatorId +
                ", topicCount=" + topicCount +
                ", postCount=" + postCount +
                ", status=" + status +
                ", sortOrder=" + sortOrder +
                '}';
    }
}
