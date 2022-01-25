package com.nicklastrange.strongmanbot.listeners;

import discord4j.core.event.domain.guild.GuildCreateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class GuildReadyEventListener {

    public Mono<Void> handle(GuildCreateEvent event) {
        log.info("Bot has connected to server with name: {}", event.getGuild().getName());
        return Mono.empty();
    }
}
