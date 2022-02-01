package com.nicklastrange.strongmanbot.listeners;

import com.nicklastrange.strongmanbot.commands.Command;
import com.nicklastrange.strongmanbot.handlers.ImageOnlyChannelEventHandler;
import com.nicklastrange.strongmanbot.service.ServerService;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Optional;

@Component
@Data
@Slf4j
public class MessageCreateEventListener {

    private final Collection<Command> commands;
    private final ServerService serverService;
    private final ImageOnlyChannelEventHandler imageOnlyChannelEventHandler;

    public MessageCreateEventListener(ApplicationContext ctx, ServerService serverService, ImageOnlyChannelEventHandler imageOnlyChannelEventHandler) {
        commands = ctx.getBeansOfType(Command.class).values();
        this.serverService = serverService;
        this.imageOnlyChannelEventHandler = imageOnlyChannelEventHandler;
    }

    public Mono<Void> handle(MessageCreateEvent event) {
        return serverService.findServerByServerId(event.getGuildId().get().asLong())
                .flatMap(server -> event
                        .getMessage()
                        .getChannel()
                        .filter(channel -> !server.getImageOnlyChannels().contains(channel.getId().asString()))
                        .flatMap(channel -> Mono.just(Optional.of(channel)))
                        .defaultIfEmpty(Optional.empty())
                        .flatMap(channel -> {
                            final Message message = event.getMessage();
                            if (message.getAuthor().isPresent() && message.getAuthor().get().isBot()) {
                                return Mono.empty();
                            }
                            if (channel.isEmpty()) {
                                return imageOnlyChannelEventHandler.handle(event);
                            }
                            final String serverPrefix = server.getServerPrefix();
                            if (!message.getContent().startsWith(serverPrefix)) {
                                return Mono.empty();
                            }
                            final String content = message.getContent();
                            for (Command command : commands) {
                                if (content.startsWith(serverPrefix +command.getName())) {
                                    log.info("Execution of command: {}", serverPrefix+command.getName());
                                    return command.execute(event);
                                }
                            }
                            return channel.get().createMessage("There is no command with given name!");
                        })
                        .then())
                .then();
    }
}
