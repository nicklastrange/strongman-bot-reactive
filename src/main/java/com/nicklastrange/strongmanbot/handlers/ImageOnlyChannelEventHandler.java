package com.nicklastrange.strongmanbot.handlers;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Attachment;
import discord4j.core.object.entity.Message;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;

@Component
public class ImageOnlyChannelEventHandler {

    private static final Set<String> IMAGE_EXTENSIONS = Set.of("jpg",
            "jpeg", "png", "gif", "webp", "tiff", "svg", "apng");


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
