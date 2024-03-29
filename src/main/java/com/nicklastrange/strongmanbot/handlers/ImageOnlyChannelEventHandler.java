package com.nicklastrange.strongmanbot.handlers;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Attachment;
import discord4j.core.object.entity.Message;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.nicklastrange.strongmanbot.util.BotConstants.IMAGE_EXTENSIONS;

@Component
public class ImageOnlyChannelEventHandler {

    public Mono<Void> handle(MessageCreateEvent event) {

        return event
                .getMessage()
                .getChannel()
                .flatMap(channel -> {
                    final Message message = event.getMessage();
                    if ((!message.getContent().isEmpty() && !message.getAttachments().isEmpty()) || !message.getContent().isEmpty()) {
                        return event.getMessage().delete();
                    }
                    for (Attachment attachment : message.getAttachments()) {
                        final String[] filename = attachment.getFilename().split("\\.");
                        if (!IMAGE_EXTENSIONS.contains(filename[1])) {
                            System.out.println(filename[1]);
                            return event.getMessage().delete();
                        }
                    }
                    return Mono.empty();
                })
                .then();
    }
}
