package com.iwe3.sec;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 智慧餐厅后台管理系统启动类
 */
@SpringBootApplication
@MapperScan("com.iwe3.sec.mapper")
public class SecApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecApplication.class, args);
    }
}
