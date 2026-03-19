package com.revshopproject.revshop;

import org.apache.logging.log4j.LogManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.revshopproject.revshop")
public class Application {

    private static final Logger log = LogManager.getLogger(Application.class);

    public static void main(String[] args) {
    	
//    	BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//        System.out.println(encoder.encode("hyderabad"));
        SpringApplication.run(Application.class, args);
        log.info("=================================================");
        log.info("  Revshop Application Started Successfully");
        log.info("  Log file location: C:/revshop-logs/revshop.log");
        log.info("=================================================");
    }
}