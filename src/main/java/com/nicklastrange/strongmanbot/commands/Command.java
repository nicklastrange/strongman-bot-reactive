package com.nicklastrange.strongmanbot.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

public interface Command {

    String getName();

    Mono<Void> execute(MessageCreateEvent event);
}
