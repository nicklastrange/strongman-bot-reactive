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
            final Mono<Server> serverByServerId = serverService.findServerByServerId(event.getGuildId().get().asLong());
            return event
                    .getMessage()
                    .getChannel()
                    .flatMap(channel -> serverByServerId
                            .flatMap(server -> channel.createMessage("Current prefix for this server is: " + server.getServerPrefix()))
                            .then()
                    );
        }
        if (messageArray.length == 2) {
            final Mono<Void> updatePrefix = serverService.findServerByServerId(event.getGuildId().get().asLong())
                    .doOnNext(server -> {
                        server.setServerPrefix(messageArray[1]);
                        serverService.updateServer(server)
                                .subscribe(s -> log.info("Database entry altered: {}", s));
                    })
                    .then();
            final Mono<Void> sendMessage = event
                    .getMessage()
                    .getChannel()
                    .flatMap(channel -> channel.createMessage("Server prefix changed to: " + messageArray[1]))
                    .then();
            return Mono.when(updatePrefix, sendMessage);
        }
        return event
                .getMessage()
                .getChannel()
                .flatMap(channel -> channel.createMessage("Bad arguments provided!"))
                .then();
    }
}
