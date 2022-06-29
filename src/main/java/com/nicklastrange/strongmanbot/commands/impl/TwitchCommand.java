package com.nicklastrange.strongmanbot.commands.impl;

import com.nicklastrange.strongmanbot.commands.Command;
import com.nicklastrange.strongmanbot.service.ServerService;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.rest.util.Permission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Set;

@Component
@Slf4j
public class TwitchCommand implements Command {

    private final ServerService serverService;

    public TwitchCommand(ServerService serverService) {
        this.serverService = serverService;
    }

    @Override
    public String getName() {
        return "twitch";
    }

    @Override
    public Set<Permission> getPermissions() {
        return Set.of(Permission.ADMINISTRATOR);
    }

    @Override
    public Mono<Void> execute(MessageCreateEvent event) {

        String[] args = event.getMessage().getContent().split(" ");
        if (args.length == 3 && args[1].equals("add")) {

            return serverService.findServerByServerId(event.getGuildId().get().asLong())
                    .doOnNext(s -> s.getTwitchStreamersToNotify().add(args[2]))
                    .flatMap(serverService::updateServer)
                    .doOnSuccess(s -> log.info("Database entry altered: {}", s))
                    .flatMap(s -> event.getMessage().getChannel())
                    .flatMap(channel -> channel.createMessage(String.format("Twitch streamer with name **%s** added to notification list!", args[2])))
                    .then();
        }
        if (args.length == 3 && args[1].equals("channel")) {
            return event.getGuild()
                    .flatMap(g -> g.getChannels()
                            .filter(item -> item.getName().equals(args[2]))
                            .last()
                    )
                    .zipWith(serverService.findServerByServerId(event.getGuildId().get().asLong()))
                    .doOnNext(tuple -> tuple.getT2().setTwitchNotificationChannel(tuple.getT1().getId().asString()))
                    .map(tuple -> tuple.getT2())
                    .flatMap(serverService::updateServer)
                    .doOnSuccess(s -> log.info("Database entry altered: {}", s))
                    .flatMap(s -> event.getMessage().getChannel())
                    .flatMap(channel -> channel.createMessage(String.format("Channel with name: **%s** added as twitch notification channel!", args[2])))
                    .then();
        }
        return Mono.empty();
    }
}