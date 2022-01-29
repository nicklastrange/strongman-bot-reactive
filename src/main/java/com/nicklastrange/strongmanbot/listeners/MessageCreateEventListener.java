package com.nicklastrange.strongmanbot.listeners;

import com.nicklastrange.strongmanbot.commands.Command;
import com.nicklastrange.strongmanbot.service.ServerService;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collection;

@Component
@Data
@Slf4j
public class MessageCreateEventListener {

    private final Collection<Command> commands;
    private final ServerService serverService;
    private final ImageOnlyChannelEventListener imageOnlyChannelEventListener;

    public MessageCreateEventListener(ApplicationContext ctx, ServerService serverService, ImageOnlyChannelEventListener imageOnlyChannelEventListener) {
        commands = ctx.getBeansOfType(Command.class).values();
        this.serverService = serverService;
        this.imageOnlyChannelEventListener = imageOnlyChannelEventListener;
    }

    public Mono<Void> handle(MessageCreateEvent event) {
        return serverService.findServerByServerId(event.getGuildId().get().asLong())
                .flatMap(server -> {
                    final Message message = event.getMessage();
                    final String serverPrefix = server.getServerPrefix();
                    //TODO: fix this!!!
                    if (Boolean.TRUE.equals(event.getMessage().getChannel()
                            .filter(channel -> server.getImageOnlyChannels().contains(channel.getId().asString()))
                            .hasElement()
                            .block())) {
                        return imageOnlyChannelEventListener.handle(event);
                    }
                    if (!message.getContent().startsWith(serverPrefix)) {
                        return Mono.empty();
                    }
                    if (message.getAuthor().isPresent() && message.getAuthor().get().isBot()) {
                        return Mono.empty();
                    }
                    final String content = message.getContent();
                    for (Command command : commands) {
                        if (content.startsWith(serverPrefix +command.getName())) {
                            log.info("Execution of command: {}", serverPrefix+command.getName());
                            return command.execute(event);
                        }
                    }
                    return message.getChannel()
                            .flatMap(channel -> channel.createMessage("There is no command with given name!"))
                            .then();
                })
                .then();
    }
}
