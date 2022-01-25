package com.nicklastrange.strongmanbot;

import com.nicklastrange.strongmanbot.listeners.GuildReadyEventListener;
import com.nicklastrange.strongmanbot.listeners.MessageCreateEventListener;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class StrongmanBotApplication {

    public static void main(String[] args) {
        final ConfigurableApplicationContext ctx = SpringApplication.run(StrongmanBotApplication.class, args);

        DiscordClientBuilder.create(System.getenv("BOT_TOKEN"))
                .build()
                .withGateway(gatewayClient -> {
                    Mono<Void> onGuildReadyEventMono = gatewayClient
                            .on(GuildCreateEvent.class, ctx.getBean(GuildReadyEventListener.class)::handle)
                            .then();
                    Mono<Void> onMessageCreateEventMono = gatewayClient
                            .on(MessageCreateEvent.class, ctx.getBean(MessageCreateEventListener.class)::handle)
                            .then();
                    return Mono.when(onGuildReadyEventMono, onMessageCreateEventMono);
                })
                .block();
    }

}
