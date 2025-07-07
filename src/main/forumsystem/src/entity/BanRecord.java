package main.forumsystem.src.entity;

import java.time.LocalDateTime;

/**
 * 用户封禁记录实体类
 * 对应数据库表：ban_records
 */
public class BanRecord {
    private int banId;
    private int userId;
    private int adminId;
    private String reason;
    private LocalDateTime banStart;
    private LocalDateTime banEnd;
    private boolean isPermanent;
    private BanStatus status;

    // 关联对象
    private User user;
    private User admin;

    /**
     * 封禁状态枚举
     */
    public enum BanStatus {
        ACTIVE("active"),
        LIFTED("lifted");

        private final String value;

        BanStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static BanStatus fromValue(String value) {
            for (BanStatus status : BanStatus.values()) {
                if (status.value.equals(value)) {
                    return status;
                }
            }
            return ACTIVE; // 默认返回活跃状态
        }
    }

    // 构造函数
    public BanRecord() {}

    public BanRecord(int userId, int adminId, String reason) {
        this.userId = userId;
        this.adminId = adminId;
        this.reason = reason;
        this.status = BanStatus.ACTIVE;
        this.isPermanent = false;
    }

    public BanRecord(int userId, int adminId, String reason, LocalDateTime banEnd) {
        this.userId = userId;
        this.adminId = adminId;
        this.reason = reason;
        this.banEnd = banEnd;
        this.status = BanStatus.ACTIVE;
        this.isPermanent = false;
    }

    public BanRecord(int userId, int adminId, String reason, boolean isPermanent) {
        this.userId = userId;
        this.adminId = adminId;
        this.reason = reason;
        this.isPermanent = isPermanent;
        this.status = BanStatus.ACTIVE;
    }

    // Getters and Setters
    public int getBanId() {
        return banId;
    }

    public void setBanId(int banId) {
        this.banId = banId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getBanStart() {
        return banStart;
    }

    public void setBanStart(LocalDateTime banStart) {
        this.banStart = banStart;
    }

    public LocalDateTime getBanEnd() {
        return banEnd;
    }

    public void setBanEnd(LocalDateTime banEnd) {
        this.banEnd = banEnd;
    }

    public boolean isPermanent() {
        return isPermanent;
    }

    public void setPermanent(boolean permanent) {
        isPermanent = permanent;
    }

    public BanStatus getStatus() {
        return status;
    }

    public void setStatus(BanStatus status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getAdmin() {
        return admin;
    }

    public void setAdmin(User admin) {
        this.admin = admin;
    }

    // 便民方法
    public boolean isActive() {
        return status == BanStatus.ACTIVE;
    }

    public boolean isLifted() {
        return status == BanStatus.LIFTED;
    }

    public boolean isExpired() {
        if (isPermanent) {
            return false;
        }
        if (banEnd == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(banEnd);
    }

    public boolean isCurrentlyActive() {
        return isActive() && !isExpired();
    }

    @Override
    public String toString() {
        return "BanRecord{" +
                "banId=" + banId +
                ", userId=" + userId +
                ", adminId=" + adminId +
                ", reason='" + reason + '\'' +
                ", banStart=" + banStart +
                ", banEnd=" + banEnd +
                ", isPermanent=" + isPermanent +
                ", status=" + status +
                '}';
    }
}

