package com.nicklastrange.strongmanbot.commands.impl;

import com.nicklastrange.strongmanbot.commands.Command;
import discord4j.core.event.domain.message.MessageCreateEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ImageCommand implements Command {
    @Override
    public String getName() {
        return "image";
    }

    @Override
    public Mono<Void> execute(MessageCreateEvent event) {
        if (event.getMessage().getContent().split(" ").length == 1) {
            return event
                    .getMessage()
                    .getChannel()
                    .flatMap(channel -> channel.createMessage("No args provided!"))
                    .then();
        }
        return event
                .getMessage()
                .getChannel()
                .flatMap(channel -> channel.createMessage("OK!"))
                .then();
    }
}
