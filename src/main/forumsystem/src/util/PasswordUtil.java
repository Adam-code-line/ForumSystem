package main.forumsystem.src.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 密码工具类
 * 提供密码加密、验证等功能
 */
public class PasswordUtil {

    private static final String SALT = "FORUM_SYSTEM_SALT_2024";

    /**
     * 加密密码
     * 
     * @param password 原始密码
     * @return 加密后的密码
     */
    public static String encrypt(String password) {
        if (password == null) {
            return null;
        }

        try {
            // 使用SHA-256加密
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            // 添加盐值
            String saltedPassword = password + SALT;
            byte[] hashedBytes = md.digest(saltedPassword.getBytes());

            // 转换为Base64字符串
            return Base64.getEncoder().encodeToString(hashedBytes);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 验证密码
     * 
     * @param password          原始密码
     * @param encryptedPassword 加密后的密码
     * @return 是否匹配
     */
    public static boolean verify(String password, String encryptedPassword) {
        if (password == null || encryptedPassword == null) {
            return false;
        }

        String encrypted = encrypt(password);
        return encrypted != null && encrypted.equals(encryptedPassword);
    }

    /**
     * 生成随机密码
     * 
     * @param length 密码长度
     * @return 随机密码
     */
    public static String generateRandomPassword(int length) {
        if (length < 6) {
            length = 6;
        }

        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }

        return password.toString();
    }
}