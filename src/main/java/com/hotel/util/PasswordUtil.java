package com.hotel.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * 密码加密：加固定盐后做 SHA-256，数据库里存的是密文，不存明文。
 */
public class PasswordUtil {

    private static final String SALT = "hotel_salt_2026";

    public static String hash(String raw) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest((raw + SALT).getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("密码加密失败", e);
        }
    }

    public static boolean matches(String raw, String hashed) {
        return hash(raw).equals(hashed);
    }
}
