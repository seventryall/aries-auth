package com.star.aries.auth.dal.config;

import com.star.aries.auth.common.util.IdWorker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;


@Configuration
public class IdWorkerConfiguration {

    @Bean
    public IdWorker getIdWorker() {
        Random random = new Random();
        IdWorker idWorker = new IdWorker(Math.abs(random.nextLong()) % 10, 1L);
        return idWorker;
    }
}
