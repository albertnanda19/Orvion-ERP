package com.orvion.hcm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HcmServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HcmServiceApplication.class, args);
    }
}
