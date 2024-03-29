package com.nicklastrange.strongmanbot.config;

import com.nicklastrange.strongmanbot.listeners.GuildReadyEventListener;
import com.nicklastrange.strongmanbot.listeners.MessageCreateEventListener;
import com.nicklastrange.strongmanbot.util.SystemUtil;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import reactor.core.scheduler.Schedulers;

@Configuration
@RequiredArgsConstructor
@Profile("!test")
public class BotConfig {

    private final ApplicationContext ctx;

    @Bean
    public GatewayDiscordClient discordClient() {
        return DiscordClientBuilder.create(SystemUtil.getenv("BOT_TOKEN"))
                .build()
                .login()
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(client -> client.on(GuildCreateEvent.class, ctx.getBean(GuildReadyEventListener.class)::handle).subscribe())
                .doOnNext(client -> client.on(MessageCreateEvent.class, ctx.getBean(MessageCreateEventListener.class)::handle).subscribe())
                .block();
    }
}
