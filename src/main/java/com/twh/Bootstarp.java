package com.twh;

import com.twh.annotation.EnableHttpServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
@EnableHttpServer
public class Bootstarp {
    public static void main(String[] args) {
        SpringApplication.run(Boolean.class, args);
    }
}
