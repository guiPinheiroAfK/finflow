package com.finflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling // ADR-0002: relay da outbox roda via @Scheduled
@SpringBootApplication
public class FinflowApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinflowApplication.class, args);
    }
}
