package com.member.storage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StorageApplication {

    public static void main(String[] args) {
        SpringApplication.run(StorageApplication.class, args);
        System.out.println("===========================================");
        System.out.println("会员储值消费系统启动成功！");
        System.out.println("服务端口: 8003");
        System.out.println("===========================================");
    }
}
