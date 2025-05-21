package com.space.destination;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class DestinationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(DestinationServiceApplication.class, args);
    }
} 