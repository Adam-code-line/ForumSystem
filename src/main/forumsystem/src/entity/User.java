package main.forumsystem.src.entity;

import java.time.LocalDateTime;

/**
 * 用户实体类
 * 对应数据库表：users
 */
public class User {
    private int userId;
    private String username;
    private String password;
    private String email;
    private String nickName;
    private String avatar;
    private UserRole role;
    private UserStatus status;
    private LocalDateTime registerTime;
    private LocalDateTime lastLogin;
    private int postCount;
    private int reputation;

    /**
     * 用户角色枚举
     */
    public enum UserRole {
        ADMIN("admin"),
        MODERATOR("moderator"),
        USER("user");

        private final String value;

        UserRole(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static UserRole fromValue(String value) {
            for (UserRole role : UserRole.values()) {
                if (role.value.equals(value)) {
                    return role;
                }
            }
            return USER; // 默认返回普通用户
        }
    }

    /**
     * 用户状态枚举
     */
    public enum UserStatus {
        ACTIVE("active"),
        BANNED("banned"),
        INACTIVE("inactive");

        private final String value;

        UserStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static UserStatus fromValue(String value) {
            for (UserStatus status : UserStatus.values()) {
                if (status.value.equals(value)) {
                    return status;
                }
            }
            return ACTIVE; // 默认返回活跃状态
        }
    }

    // 构造函数
    public User() {}

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = UserRole.USER;
        this.status = UserStatus.ACTIVE;
        this.postCount = 0;
        this.reputation = 0;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public LocalDateTime getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(LocalDateTime registerTime) {
        this.registerTime = registerTime;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public int getPostCount() {
        return postCount;
    }

    public void setPostCount(int postCount) {
        this.postCount = postCount;
    }

    public int getReputation() {
        return reputation;
    }

    public void setReputation(int reputation) {
        this.reputation = reputation;
    }

    // 便民方法
    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }

    public boolean isModerator() {
        return role == UserRole.MODERATOR;
    }

    public boolean isBanned() {
        return status == UserStatus.BANNED;
    }

    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", nickName='" + nickName + '\'' +
                ", role=" + role +
                ", status=" + status +
                ", postCount=" + postCount +
                ", reputation=" + reputation +
                '}';
    }
}
