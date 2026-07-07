package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ScholarStream - Open-Source Research Paper Repository.
 *
 * Entry point of the Spring Boot application. Boots the embedded
 * Tomcat server, wires up the JPA repositories, and applies the
 * security configuration declared in WebSecurityConfig.
 */
@SpringBootApplication
public class ScholarstreamApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScholarstreamApplication.class, args);
    }

}
