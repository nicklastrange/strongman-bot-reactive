package com.nicklastrange.strongmanbot.config;

import com.nicklastrange.strongmanbot.listeners.GuildReadyEventListener;
import com.nicklastrange.strongmanbot.listeners.MessageCreateEventListener;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BotConfig {

    private final ApplicationContext ctx;

    @Bean
    public GatewayDiscordClient discordClient() {
        GatewayDiscordClient discordClient = DiscordClientBuilder.create(System.getenv("BOT_TOKEN"))
                .build()
                .login()
                .block();

        discordClient.on(GuildCreateEvent.class, ctx.getBean(GuildReadyEventListener.class)::handle)
                .subscribe();
        discordClient.on(MessageCreateEvent.class, ctx.getBean(MessageCreateEventListener.class)::handle)
                .subscribe();

        return discordClient;
    }
}
