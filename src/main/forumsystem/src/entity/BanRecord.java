package main.forumsystem.src.entity;

import java.time.LocalDateTime;

/**
 * 用户封禁记录实体类
 * 对应数据库表：ban_records
 * 用于记录用户的封禁信息，包括封禁原因、封禁时间、封禁状态等。
 */
public class BanRecord {
    private int banId; // 封禁记录的唯一标识
    private int userId; // 被封禁用户的ID
    private int adminId; // 执行封禁操作的管理员ID
    private String reason; // 封禁原因
    private LocalDateTime banStart; // 封禁开始时间
    private LocalDateTime banEnd; // 封禁结束时间（如果是临时封禁）
    private boolean isPermanent; // 是否为永久封禁
    private BanStatus status; // 封禁状态（ACTIVE或LIFTED）

    // 关联对象
    private User user; // 被封禁的用户对象
    private User admin; // 执行封禁操作的管理员对象

    /**
     * 封禁状态枚举
     * 表示封禁记录的当前状态。
     */
    public enum BanStatus {
        ACTIVE("active"), // 封禁状态为活跃
        LIFTED("lifted"); // 封禁状态为解除

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

    /**
     * 默认构造函数
     * 用于创建一个空的封禁记录对象。
     */
    public BanRecord() {}

    /**
     * 构造函数
     * 创建一个基本的封禁记录，仅包含用户ID、管理员ID和封禁原因。
     * 默认设置封禁状态为ACTIVE，且封禁不是永久的。
     * 
     * @param userId 被封禁用户的ID
     * @param adminId 执行封禁操作的管理员ID
     * @param reason 封禁原因
     */
    public BanRecord(int userId, int adminId, String reason) {
        this.userId = userId;
        this.adminId = adminId;
        this.reason = reason;
        this.status = BanStatus.ACTIVE;
        this.isPermanent = false;
    }

    /**
     * 构造函数
     * 创建一个带有封禁结束时间的记录。
     * 默认设置封禁状态为ACTIVE，且封禁不是永久的。
     * 
     * @param userId 被封禁用户的ID
     * @param adminId 执行封禁操作的管理员ID
     * @param reason 封禁原因
     * @param banEnd 封禁结束时间
     */
    public BanRecord(int userId, int adminId, String reason, LocalDateTime banEnd) {
        this.userId = userId;
        this.adminId = adminId;
        this.reason = reason;
        this.banEnd = banEnd;
        this.status = BanStatus.ACTIVE;
        this.isPermanent = false;
    }

    /**
     * 构造函数
     * 创建一个永久封禁记录。
     * 默认设置封禁状态为ACTIVE。
     * 
     * @param userId 被封禁用户的ID
     * @param adminId 执行封禁操作的管理员ID
     * @param reason 封禁原因
     * @param isPermanent 是否为永久封禁
     */
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

    /**
     * 判断封禁记录是否处于活跃状态。
     * @return 如果状态为ACTIVE，则返回true；否则返回false。
     */
    public boolean isActive() {
        return status == BanStatus.ACTIVE;
    }

    /**
     * 判断封禁记录是否已解除。
     * @return 如果状态为LIFTED，则返回true；否则返回false。
     */
    public boolean isLifted() {
        return status == BanStatus.LIFTED;
    }

    /**
     * 判断封禁记录是否已过期。
     * 如果是永久封禁或未设置结束时间，则不会过期。
     * @return 如果封禁已过期，则返回true；否则返回false。
     */
    public boolean isExpired() {
        if (isPermanent) {
            return false;
        }
        if (banEnd == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(banEnd);
    }

    /**
     * 判断封禁记录是否当前仍然有效。
     * 封禁记录必须处于活跃状态且未过期。
     * @return 如果封禁记录仍然有效，则返回true；否则返回false。
     */
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

