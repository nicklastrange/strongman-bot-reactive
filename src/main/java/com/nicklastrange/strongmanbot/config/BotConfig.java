package com.nicklastrange.strongmanbot.config;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.boot.r2dbc.ConnectionFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BotConfig {

    @Bean
    public ConnectionFactory connectionFactory() {
        return ConnectionFactoryBuilder
                .withUrl(System.getenv("DATABASE_URL"))
                .username(System.getenv("DATABASE_USERNAME"))
                .password(System.getenv("DATABASE_PASSWORD"))
                .build();
    }
}
