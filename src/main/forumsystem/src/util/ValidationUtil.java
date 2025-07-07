package main.forumsystem.src.util;

import java.util.regex.Pattern;

/**
 * 验证工具类
 * 提供各种数据验证功能
 */
public class ValidationUtil {
    
    // 邮箱验证正则表达式
    private static final String EMAIL_REGEX = 
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    
    // 用户名验证正则表达式（字母、数字、下划线，3-20位）
    private static final String USERNAME_REGEX = "^[a-zA-Z0-9_]{3,20}$";
    
    // 密码验证正则表达式（至少6位，包含字母和数字）
    private static final String PASSWORD_REGEX = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{6,}$";
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private static final Pattern USERNAME_PATTERN = Pattern.compile(USERNAME_REGEX);
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);
    
    /**
     * 验证邮箱格式
     * @param email 邮箱地址
     * @return 是否有效
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    /**
     * 验证用户名格式
     * @param username 用户名
     * @return 是否有效
     */
    public static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return USERNAME_PATTERN.matcher(username.trim()).matches();
    }
    
    /**
     * 验证密码强度
     * @param password 密码
     * @return 是否有效
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(password).matches();
    }
    
    /**
     * 验证字符串是否为空
     * @param str 字符串
     * @return 是否为空
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * 验证字符串长度
     * @param str 字符串
     * @param minLength 最小长度
     * @param maxLength 最大长度
     * @return 是否在有效范围内
     */
    public static boolean isValidLength(String str, int minLength, int maxLength) {
        if (str == null) {
            return false;
        }
        int length = str.trim().length();
        return length >= minLength && length <= maxLength;
    }
    
    /**
     * 验证数字范围
     * @param num 数字
     * @param min 最小值
     * @param max 最大值
     * @return 是否在有效范围内
     */
    public static boolean isValidRange(int num, int min, int max) {
        return num >= min && num <= max;
    }
    
    /**
     * 验证昵称格式（允许中文、英文、数字、特殊字符，2-20位）
     * @param nickName 昵称
     * @return 是否有效
     */
    public static boolean isValidNickName(String nickName) {
        if (nickName == null || nickName.trim().isEmpty()) {
            return false;
        }
        String trimmed = nickName.trim();
        return trimmed.length() >= 2 && trimmed.length() <= 20;
    }
    
    /**
     * 验证头像URL格式
     * @param avatarUrl 头像URL
     * @return 是否有效
     */
    public static boolean isValidAvatarUrl(String avatarUrl) {
        if (avatarUrl == null || avatarUrl.trim().isEmpty()) {
            return true; // 头像可以为空
        }
        
        String trimmed = avatarUrl.trim();
        return trimmed.startsWith("http://") || trimmed.startsWith("https://") || 
               trimmed.startsWith("/") || trimmed.startsWith("data:image/");
    }
    
    /**
     * 验证分页参数
     * @param page 页码
     * @param size 每页大小
     * @return 是否有效
     */
    public static boolean isValidPageParams(int page, int size) {
        return page > 0 && size > 0 && size <= 100;
    }
    
    /**
     * 清理和验证搜索关键词
     * @param keyword 搜索关键词
     * @return 清理后的关键词，如果无效返回null
     */
    public static String cleanSearchKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return null;
        }
        
        String cleaned = keyword.trim();
        if (cleaned.length() < 1 || cleaned.length() > 50) {
            return null;
        }
        
        // 移除特殊字符，防止SQL注入
        cleaned = cleaned.replaceAll("[<>\"'%;()&+]", "");
        
        return cleaned.isEmpty() ? null : cleaned;
    }
}
