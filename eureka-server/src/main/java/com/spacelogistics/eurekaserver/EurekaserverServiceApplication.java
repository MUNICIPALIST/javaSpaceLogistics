package com.spacelogistics.eurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
// Removed GetMapping and RestController imports

@SpringBootApplication
@EnableEurekaServer
public class EurekaserverServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaserverServiceApplication.class, args);
    }
    
    // Removed conflicting HelloController
}
