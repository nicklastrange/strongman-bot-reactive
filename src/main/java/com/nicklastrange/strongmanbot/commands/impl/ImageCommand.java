package com.nicklastrange.strongmanbot.commands.impl;

import com.nicklastrange.strongmanbot.commands.Command;
import com.nicklastrange.strongmanbot.model.Server;
import com.nicklastrange.strongmanbot.service.ServerService;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.GuildChannel;
import discord4j.rest.util.Permission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Set;
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
            return listImageOnlyChannels(event);
        }
        if (messageArray.length == 3) {
            if (messageArray[1].equals("add")) {
                return addImageOnlyChannel(event, messageArray[2]);
            }
            if (messageArray[1].equals("remove")) {
                return removeImageChannel(event, messageArray[2]);
            }
        }
        return event
                .getMessage()
                .getChannel()
                .flatMap(channel -> channel.createMessage("Bad arguments provided!"))
                .then();
    }

    private Mono<Void> listImageOnlyChannels(MessageCreateEvent event) {

        return Flux.zip(serverService.findServerByServerId(event.getGuildId().get().asLong()), event.getMessage().getChannel(), event.getGuild())
                .flatMap(tuple -> {
                    if (tuple.getT1().getImageOnlyChannels().isEmpty()) {
                        return tuple.getT2().createMessage("No channel is set as **image-only** on this server!");
                    }
                    StringBuilder response = new StringBuilder("```\n");
                    return tuple.getT3()
                            .getChannels()
                            .filter(guildChannel -> tuple.getT1().getImageOnlyChannels().contains(guildChannel.getId().asString()))
                            .doOnNext(guildChannel -> response.append("- ").append(guildChannel.getName()).append("\n"))
                            .doOnComplete(() -> response.append("```\n"))
                            .publishOn(Schedulers.boundedElastic())
                            .doOnComplete(() -> tuple.getT2().createMessage("Channel(s) that are set as **image-only**: \n" + response).subscribe());
                })
                .then();
    }

    public Mono<Void> addImageOnlyChannel(MessageCreateEvent event, String channelName) {
        final Mono<Server> serverByServerId = serverService.findServerByServerId(event.getGuildId().get().asLong());
        return event
                .getMessage()
                .getChannel()
                .flatMap(channel -> event.getGuild()
                        .flatMap(guild -> guild.getChannels()
                                .filter(guildChannel -> guildChannel.getName().equals(channelName))
                                .flatMap(guildChannel -> serverByServerId
                                        .flatMap(server -> {
                                            final Set<String> imageOnlyChannels = server.getImageOnlyChannels();
                                            //TODO: add logic that checks if two sets are same and send message to discord channel, equals does not work
                                            imageOnlyChannels.add(guildChannel.getId().asString());
                                            server.setImageOnlyChannels(imageOnlyChannels);
                                            serverService.updateServer(server)
                                                    .subscribe(s -> log.info("Database entry altered: {}", s));
                                            return channel.createMessage(String.format("Channel with name **%s** is now **image-only**!", channelName));
                                        })
                                )
                                .switchIfEmpty(guildChannel -> channel.createMessage("There is no channel with given name!"))
                                .then()
                        ));
    }

    public Mono<Void> removeImageChannel(MessageCreateEvent event, String channelName) {
        final Mono<Server> serverByServerId = serverService.findServerByServerId(event.getGuildId().get().asLong());
        return event
                .getMessage()
                .getChannel()
                .flatMap(channel -> event.getGuild()
                        .flatMap(guild -> guild.getChannels()
                                .filter(guildChannel -> guildChannel.getName().equals(channelName))
                                .flatMap(guildChannel -> serverByServerId
                                        .flatMap(server -> {
                                            final Set<String> imageOnlyChannels = server.getImageOnlyChannels();
                                            imageOnlyChannels.remove(guildChannel.getId().asString());
                                            server.setImageOnlyChannels(imageOnlyChannels);
                                            serverService.updateServer(server)
                                                    .subscribe(s -> log.info("Database entry altered: {}", s));
                                            return channel.createMessage(String.format("Channel with name **%s** is not **image-only** channel anymore!", channelName));
                                        })
                                )
                                .switchIfEmpty(guildChannel -> channel.createMessage("There is no channel with given name!"))
                                .then()
                        ));
    }
}
