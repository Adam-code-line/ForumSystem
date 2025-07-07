package main.forumsystem.src.entity;

import java.time.LocalDateTime;

/**
 * 回复实体类
 * 对应数据库表：replies
 */
public class Reply {
    private int replyId;
    private int topicId;
    private int userId;
    private String content;
    private LocalDateTime createTime;
    private ReplyStatus status;
    private int replyToId; // 回复的目标回复ID，用于嵌套回复

    // 关联对象
    private User author;
    private Topic topic;
    private Reply replyTo; // 被回复的回复

    /**
     * 回复状态枚举
     */
    public enum ReplyStatus {
        NORMAL("normal"),
        DELETED("deleted"),
        HIDDEN("hidden");

        private final String value;

        ReplyStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static ReplyStatus fromValue(String value) {
            for (ReplyStatus status : ReplyStatus.values()) {
                if (status.value.equals(value)) {
                    return status;
                }
            }
            return NORMAL; // 默认返回正常状态
        }
    }

    // 构造函数
    public Reply() {}

    public Reply(int topicId, int userId, String content) {
        this.topicId = topicId;
        this.userId = userId;
        this.content = content;
        this.status = ReplyStatus.NORMAL;
        this.replyToId = 0; // 0表示不是回复其他回复
    }

    public Reply(int topicId, int userId, String content, int replyToId) {
        this.topicId = topicId;
        this.userId = userId;
        this.content = content;
        this.status = ReplyStatus.NORMAL;
        this.replyToId = replyToId;
    }

    // Getters and Setters
    public int getReplyId() {
        return replyId;
    }

    public void setReplyId(int replyId) {
        this.replyId = replyId;
    }

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public ReplyStatus getStatus() {
        return status;
    }

    public void setStatus(ReplyStatus status) {
        this.status = status;
    }

    public int getReplyToId() {
        return replyToId;
    }

    public void setReplyToId(int replyToId) {
        this.replyToId = replyToId;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public Reply getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(Reply replyTo) {
        this.replyTo = replyTo;
    }

    // 便民方法
    public boolean isNormal() {
        return status == ReplyStatus.NORMAL;
    }

    public boolean isDeleted() {
        return status == ReplyStatus.DELETED;
    }

    public boolean isHidden() {
        return status == ReplyStatus.HIDDEN;
    }

    public boolean isNestedReply() {
        return replyToId > 0;
    }

    @Override
    public String toString() {
        return "Reply{" +
                "replyId=" + replyId +
                ", topicId=" + topicId +
                ", userId=" + userId +
                ", content='" + content + '\'' +
                ", createTime=" + createTime +
                ", status=" + status +
                ", replyToId=" + replyToId +
                '}';
    }
}
