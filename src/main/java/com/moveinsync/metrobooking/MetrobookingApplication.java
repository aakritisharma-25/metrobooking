package com.moveinsync.metrobooking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MetrobookingApplication {

    public static void main(String[] args) {
        SpringApplication.run(MetrobookingApplication.class, args);
    }
}