package com.frameforward.bootstrap;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.frameforward")
@MapperScan("com.frameforward")
public class FrameForwardApplication {

    public static void main(String[] args) {
        SpringApplication.run(FrameForwardApplication.class, args);
    }
}
