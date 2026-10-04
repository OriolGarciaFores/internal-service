package com.tebra.internalservice;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
public class InternalServiceApplication {

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Madrid"));
    }

    public static void main(String[] args) {
        SpringApplication.run(InternalServiceApplication.class, args);
    }

}
