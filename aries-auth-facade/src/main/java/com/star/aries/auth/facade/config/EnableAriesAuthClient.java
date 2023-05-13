package com.star.aries.auth.facade.config;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({ClientResourceServerConfig.class})
public @interface EnableAriesAuthClient {
}
