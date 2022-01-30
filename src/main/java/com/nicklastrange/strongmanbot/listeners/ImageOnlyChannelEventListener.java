package com.nicklastrange.strongmanbot.listeners;

import discord4j.core.event.domain.message.MessageCreateEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ImageOnlyChannelEventListener {


    public Mono<Void> handle(MessageCreateEvent event) {

        return event
                .getMessage()
                .getChannel()
                .flatMap(channel -> channel.createMessage("OK!"))
                .then();
    }
}
