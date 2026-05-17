package com.orvion.sales;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SalesCrmServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(SalesCrmServiceApplication.class, args);
    }
}
