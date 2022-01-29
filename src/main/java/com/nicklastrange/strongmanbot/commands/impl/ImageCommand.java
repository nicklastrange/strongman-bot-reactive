package com.nicklastrange.strongmanbot.commands.impl;

import com.nicklastrange.strongmanbot.commands.Command;
import com.nicklastrange.strongmanbot.model.Server;
import com.nicklastrange.strongmanbot.service.ServerService;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.GuildChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ImageCommand implements Command {

    private final ServerService serverService;

    public ImageCommand(ServerService serverService) {
        this.serverService = serverService;
    }

    @Override
    public String getName() {
        return "image";
    }

    @Override
    public Mono<Void> execute(MessageCreateEvent event) {
        final String[] messageArray = event.getMessage().getContent().split(" ");
        final Mono<Server> serverByServerId = serverService.findServerByServerId(event.getGuildId().get().asLong());
        if (messageArray.length == 1) {
            return event
                    .getMessage()
                    .getChannel()
                    .flatMap(channel -> serverByServerId
                            .flatMap(server -> {
                                if (server.getImageOnlyChannels().isEmpty()) {
                                    return channel.createMessage("No channel is set as **image-only** on this server!");
                                }
                                final List<String> imageOnlyChannels = server.getImageOnlyChannels();
                                return event.getGuild()
                                        .flatMap(guild -> guild.getChannels()
                                                .filter(guildChannel -> imageOnlyChannels.contains(guildChannel.getId().asString()))
                                                .collect(Collectors.toList())
                                                .flatMap(list -> {
                                                    StringBuilder stringBuilder = new StringBuilder();
                                                    stringBuilder.append("```\n");
                                                    for (GuildChannel guildChannel : list) {
                                                        String tmp = "- " + guildChannel.getName() + "\n";
                                                        stringBuilder.append(tmp);
                                                    }
                                                    stringBuilder.append("```\n");
                                                    return channel.createMessage("Channel(s) that are set as **image-only**: \n" + stringBuilder);
                                                }));
                            })
                            .then());
        }
        if (messageArray.length == 3) {
            if (messageArray[1].equals("add")) {
                return event
                        .getMessage()
                        .getChannel()
                        .flatMap(channel -> event.getGuild()
                                .flatMap(guild -> guild.getChannels()
                                        .filter(guildChannel -> guildChannel.getName().equals(messageArray[2]))
                                                .flatMap(guildChannel -> serverByServerId
                                                        .flatMap(server -> {
                                                            final List<String> imageOnlyChannels = server.getImageOnlyChannels();
                                                            imageOnlyChannels.add(guildChannel.getId().asString());
                                                            server.setImageOnlyChannels(imageOnlyChannels);
                                                            serverService.updateServer(server)
                                                                    .subscribe(s -> log.info("Database entry altered: {}", s));
                                                            return channel.createMessage(String.format("Channel with name **%s** is now **image-only**", messageArray[2]));
                                                        })
                                                )
                                                .switchIfEmpty(guildChannel -> channel.createMessage("There is no channel with given name!"))
                                                .then()
                                ));
            }
        }

        return event
                .getMessage()
                .getChannel()
                .flatMap(channel -> channel.createMessage("Bad arguments provided!"))
                .then();
    }
}
