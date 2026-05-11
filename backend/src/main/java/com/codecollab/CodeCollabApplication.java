package com.codecollab;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.codecollab.**.mapper")
@SpringBootApplication
public class CodeCollabApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodeCollabApplication.class, args);
    }
}
