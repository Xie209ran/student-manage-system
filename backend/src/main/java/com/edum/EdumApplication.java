package com.edum;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 教学数字平台后台管理系统启动类
 *
 * @author EDUM
 * @since 2026-05-19
 */
@SpringBootApplication
@MapperScan("com.edum.mapper")
public class EdumApplication {

    public static void main(String[] args) {
        SpringApplication.run(EdumApplication.class, args);
    }
}
