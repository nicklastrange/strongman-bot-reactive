package com.nicklastrange.strongmanbot.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.rest.util.Permission;
import reactor.core.publisher.Mono;

import java.util.Set;

public interface Command {

    String getName();

    default Set<Permission> getPermissions() {
        return Set.of();
    };

    Mono<Void> execute(MessageCreateEvent event);
}
