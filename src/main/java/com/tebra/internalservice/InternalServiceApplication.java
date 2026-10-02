package com.tebra.internalservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class InternalServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InternalServiceApplication.class, args);
    }

}
