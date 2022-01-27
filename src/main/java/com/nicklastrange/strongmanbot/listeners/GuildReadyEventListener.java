package com.nicklastrange.strongmanbot.listeners;

import com.nicklastrange.strongmanbot.model.Server;
import com.nicklastrange.strongmanbot.service.ServerService;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class GuildReadyEventListener {

    private final ServerService serverService;
    private final String defaultPrefix = "!";

    public GuildReadyEventListener(ServerService serverService) {
        this.serverService = serverService;
    }

    public Mono<Void> handle(GuildCreateEvent event) {

        final long serverId = event.getGuild().getId().asLong();
        final String serverName = event.getGuild().getName();

        return serverService.findServerByServerId(serverId)
                .doOnNext(s -> {
                    log.info("Bot has connected to server with name: {}", serverName);
                })
                .doOnError(e -> {
                    log.info("Bot has connected to new server, creating database entry!");
                    final Server server = Server.builder()
                            .serverId(serverId)
                            .serverName(serverName)
                            .serverPrefix(defaultPrefix)
                            .build();
                    serverService.addNewServer(server)
                            .subscribe(s -> log.info("Database entry created: {}", s));
                })
                .then();
    }
}
