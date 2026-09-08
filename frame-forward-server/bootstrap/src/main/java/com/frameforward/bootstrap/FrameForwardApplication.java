package com.frameforward.bootstrap;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.frameforward")
@MapperScan(basePackages = "com.frameforward", annotationClass = org.apache.ibatis.annotations.Mapper.class)
public class FrameForwardApplication {

    public static void main(String[] args) {
        SpringApplication.run(FrameForwardApplication.class, args);
    }
}
