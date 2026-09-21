package com.jose.pruebams4m;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PruebaMs4mApplication {

    public static void main(String[] args) {
        SpringApplication.run(
                PruebaMs4mApplication.class,
                args
        );
    }
}