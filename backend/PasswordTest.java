package com.edum;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 测试BCrypt密码验证
 */
public class PasswordTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String inputPassword = "admin123";
        String storedHash = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi";
        
        System.out.println("输入的密码: " + inputPassword);
        System.out.println("存储的hash: " + storedHash);
        System.out.println("hash长度: " + storedHash.length());
        System.out.println();
        
        // 测试密码匹配
        boolean matches = encoder.matches(inputPassword, storedHash);
        System.out.println("密码匹配结果: " + matches);
        
        if (matches) {
            System.out.println("✅ 密码正确！");
        } else {
            System.out.println("❌ 密码错误！");
            System.out.println();
            System.out.println("生成一个新的BCrypt hash:");
            String newHash = encoder.encode(inputPassword);
            System.out.println("新hash: " + newHash);
            System.out.println("新hash长度: " + newHash.length());
        }
    }
}
