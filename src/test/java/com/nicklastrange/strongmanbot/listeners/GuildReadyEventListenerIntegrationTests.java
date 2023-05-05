package com.nicklastrange.strongmanbot.listeners;

import com.nicklastrange.strongmanbot.AbstractTestContainersIntegrationTest;
import com.nicklastrange.strongmanbot.model.Server;
import com.nicklastrange.strongmanbot.service.ServerService;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import discord4j.core.object.entity.Guild;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import reactor.test.StepVerifier;

import java.util.NoSuchElementException;

import static com.nicklastrange.strongmanbot.util.BotConstants.DEFAULT_PREFIX;
import static org.mockito.Mockito.when;

@Import({ GuildReadyEventListener.class, ServerService.class })
public class GuildReadyEventListenerIntegrationTests extends AbstractTestContainersIntegrationTest {

    @Autowired
    GuildReadyEventListener guildReadyEventListener;
    @Autowired
    ServerService serverService;
    @Mock
    GuildCreateEvent guildCreateEvent;
    @Mock
    Guild guild;
    @Mock
    Snowflake snowflake;

    @Test
    public void testHandleGuildCreateEvent_withNewServer() {
        long newGuildId = 5555L;
        String newGuildName = "Test Server 1";

        //Check if server not exists
        serverService.findServerByServerId(newGuildId)
                .as(StepVerifier::create)
                .expectError(NoSuchElementException.class)
                .verify();

        when(guildCreateEvent.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn(snowflake);
        when(snowflake.asLong()).thenReturn(newGuildId);
        when(guild.getName()).thenReturn(newGuildName);

        guildReadyEventListener.handle(guildCreateEvent)
                .as(StepVerifier::create)
                .verifyComplete();

        serverService.findServerByServerId(newGuildId)
                .as(StepVerifier::create)
                .expectNextMatches(server ->
                                server.getServerId().equals(newGuildId) &&
                                server.getServerName().equals(newGuildName)
                        )
                .verifyComplete();
    }

    @Test
    public void testHandleGuildCreateEvent_withExistingServer() {
        long existingGuildId = 5555L;
        String existingGuildName = "Test Server 1";

        Server existingServer = Server.builder()
                .serverId(existingGuildId)
                .serverName(existingGuildName)
                .serverPrefix(DEFAULT_PREFIX)
                .build();

        when(guildCreateEvent.getGuild()).thenReturn(guild);
        when(guild.getId()).thenReturn(snowflake);
        when(snowflake.asLong()).thenReturn(existingGuildId);
        when(guild.getName()).thenReturn(existingGuildName);

        serverService.addNewServer(existingServer)
                .flatMap(item -> guildReadyEventListener.handle(guildCreateEvent))
                .as(StepVerifier::create)
                .verifyComplete();
    }
}
