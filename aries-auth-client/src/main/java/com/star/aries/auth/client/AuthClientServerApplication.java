package com.star.aries.auth.client;

import com.star.aries.auth.facade.config.EnableAriesAuthClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@EnableAriesAuthClient
public class AuthClientServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthClientServerApplication.class,args);
    }
}
