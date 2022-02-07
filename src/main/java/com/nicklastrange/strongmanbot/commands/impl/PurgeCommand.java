package com.nicklastrange.strongmanbot.commands.impl;

import com.nicklastrange.strongmanbot.commands.Command;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.rest.util.Permission;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Set;

@Component
public class PurgeCommand implements Command {

    @Override
    public String getName() {
        return "purge";
    }

    @Override
    public Set<Permission> getPermissions() {
        return Set.of(
                Permission.MANAGE_CHANNELS,
                Permission.ADMINISTRATOR
        );
    }

    @Override
    public Mono<Void> execute(MessageCreateEvent event) {
        final String[] messageArray = event.getMessage().getContent().split(" ");
        if (messageArray.length == 1) {
            return event
                    .getMessage()
                    .getChannel()
                    .flatMap(channel -> channel.createMessage("No arguments provided!"))
                    .then();
        }
        if (messageArray.length == 2) {
            if (NumberUtils.isParsable(messageArray[1])) {
                return event
                        .getMessage()
                        .getChannel()
                        .flatMap(channel -> {
                            if (channel instanceof TextChannel) {
                                final Mono<Void> purge = ((TextChannel) channel)
                                        .bulkDeleteMessages(channel
                                                .getMessagesBefore(event.getMessage().getId())
                                                .take(Long.parseLong(messageArray[1])))
                                        .then();
                                final Mono<Void> response = channel.createMessage(String.format("Successfully purged **%s** messages!", messageArray[1])).then();
                                return Mono.when(purge, response);
                            }
                            return channel.createMessage("Channel is not text channel!").then();
                        });
            }
            return event
                    .getMessage()
                    .getChannel()
                    .flatMap(channel -> channel.createMessage("Bad argument provided! **" + messageArray[1] + "** is not a number!"))
                    .then();
        }
        return event
                .getMessage()
                .getChannel()
                .flatMap(channel -> channel.createMessage("Too much arguments provided!"))
                .then();
    }
}
