package com.nicklastrange.strongmanbot.commands.impl;

import com.nicklastrange.strongmanbot.commands.Command;
import com.nicklastrange.strongmanbot.model.Server;
import com.nicklastrange.strongmanbot.service.ServerService;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.rest.util.Permission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Set;

@Component
@Slf4j
public class PrefixCommand implements Command {

    private final ServerService serverService;

    public PrefixCommand(ServerService serverService) {
        this.serverService = serverService;
    }

    @Override
    public String getName() {
        return "prefix";
    }

    @Override
    public Set<Permission> getPermissions() {
        return Set.of(Permission.ADMINISTRATOR);
    }

    @Override
    public Mono<Void> execute(MessageCreateEvent event) {
        final String[] messageArray = event.getMessage().getContent().split(" ");
        if (messageArray.length == 1) {
            return serverService.findServerByServerId(event.getGuildId().get().asLong())
                    .zipWith(event.getMessage().getChannel())
                    .flatMap(tuple -> tuple.getT2().createMessage("Current prefix for this server is: " + tuple.getT1().getServerPrefix()))
                    .then();
        }
        if (messageArray.length == 2) {
            return serverService.findServerByServerId(event.getGuildId().get().asLong())
                    .doOnNext(server -> server.setServerPrefix(messageArray[1]))
                    .flatMap(serverService::updateServer)
                    .doOnSuccess(s -> log.info("Database entry altered: {}", s))
                    .flatMap(s -> event.getMessage().getChannel())
                    .flatMap(channel -> channel.createMessage("Server prefix changed to: " + messageArray[1]))
                    .then();
        }
        return event
                .getMessage()
                .getChannel()
                .flatMap(channel -> channel.createMessage("Bad arguments provided!"))
                .then();
    }
}
