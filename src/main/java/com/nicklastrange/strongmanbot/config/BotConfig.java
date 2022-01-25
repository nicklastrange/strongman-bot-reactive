package com.nicklastrange.strongmanbot.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BotConfig {

    private final ApplicationContext ctx;

    public BotConfig(ApplicationContext ctx) {
        this.ctx = ctx;
    }
}
