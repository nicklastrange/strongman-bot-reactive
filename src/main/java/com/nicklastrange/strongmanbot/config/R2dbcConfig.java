package com.nicklastrange.strongmanbot.config;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.r2dbc.ConnectionFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({ R2dbcProperties.class })
public class R2dbcConfig {

    @Bean
    public ConnectionFactory connectionFactory(R2dbcProperties r2dbcProperties) {
        String databaseUrl = r2dbcProperties.getUrl();
        String databaseUsername = r2dbcProperties.getUsername();
        String databasePassword = r2dbcProperties.getPassword();

        return ConnectionFactoryBuilder
                .withUrl(databaseUrl)
                .username(databaseUsername)
                .password(databasePassword)
                .build();
    }
}
