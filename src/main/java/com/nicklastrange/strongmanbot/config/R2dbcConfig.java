package com.nicklastrange.strongmanbot.config;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.boot.r2dbc.ConnectionFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class R2dbcConfig {

    @Bean
    public ConnectionFactory connectionFactory() {
        String databaseUrl = "r2dbc:" + System.getenv("DATABASE_URL");
        String databaseUsername = System.getenv("DATABASE_USERNAME");
        String databasePassword = System.getenv("DATABASE_PASSWORD");

        return ConnectionFactoryBuilder
                .withUrl(databaseUrl)
                .username(databaseUsername)
                .password(databasePassword)
                .build();
    }
}
