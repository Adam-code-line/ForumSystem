package main.forumsystem.src.entity;

import java.time.LocalDateTime;

/**
 * 主题帖实体类
 * 对应数据库表：topics
 */
public class Topic {
    private int topicId;
    private int forumId;
    private int userId;
    private String title;
    private String content;
    private boolean isPinned;
    private boolean isLocked;
    private int viewCount;
    private int replyCount;
    private LocalDateTime createTime;
    private LocalDateTime lastReplyTime;
    private int lastReplyUserId;
    private TopicStatus status;

    // 关联对象
    private User author;
    private Forum forum;
    private User lastReplyUser;

    /**
     * 主题状态枚举
     */
    public enum TopicStatus {
        NORMAL("normal"),
        DELETED("deleted"),
        HIDDEN("hidden");

        private final String value;

        TopicStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static TopicStatus fromValue(String value) {
            for (TopicStatus status : TopicStatus.values()) {
                if (status.value.equals(value)) {
                    return status;
                }
            }
            return NORMAL; // 默认返回正常状态
        }
    }

    // 构造函数
    public Topic() {}

    public Topic(int forumId, int userId, String title, String content) {
        this.forumId = forumId;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.status = TopicStatus.NORMAL;
        this.isPinned = false;
        this.isLocked = false;
        this.viewCount = 0;
        this.replyCount = 0;
    }

    // Getters and Setters
    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public int getForumId() {
        return forumId;
    }

    public void setForumId(int forumId) {
        this.forumId = forumId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getLastReplyTime() {
        return lastReplyTime;
    }

    public void setLastReplyTime(LocalDateTime lastReplyTime) {
        this.lastReplyTime = lastReplyTime;
    }

    public int getLastReplyUserId() {
        return lastReplyUserId;
    }

    public void setLastReplyUserId(int lastReplyUserId) {
        this.lastReplyUserId = lastReplyUserId;
    }

    public TopicStatus getStatus() {
        return status;
    }

    public void setStatus(TopicStatus status) {
        this.status = status;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Forum getForum() {
        return forum;
    }

    public void setForum(Forum forum) {
        this.forum = forum;
    }

    public User getLastReplyUser() {
        return lastReplyUser;
    }

    public void setLastReplyUser(User lastReplyUser) {
        this.lastReplyUser = lastReplyUser;
    }

    // 便民方法
    public boolean isNormal() {
        return status == TopicStatus.NORMAL;
    }

    public boolean isDeleted() {
        return status == TopicStatus.DELETED;
    }

    public boolean isHidden() {
        return status == TopicStatus.HIDDEN;
    }

    public void incrementViewCount() {
        this.viewCount++;
    }

    public void incrementReplyCount() {
        this.replyCount++;
    }

    public void decrementReplyCount() {
        if (this.replyCount > 0) {
            this.replyCount--;
        }
    }

    @Override
    public String toString() {
        return "Topic{" +
                "topicId=" + topicId +
                ", forumId=" + forumId +
                ", userId=" + userId +
                ", title='" + title + '\'' +
                ", isPinned=" + isPinned +
                ", isLocked=" + isLocked +
                ", viewCount=" + viewCount +
                ", replyCount=" + replyCount +
                ", status=" + status +
                '}';
    }
}
