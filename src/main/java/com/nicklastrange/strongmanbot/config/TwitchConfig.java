package com.nicklastrange.strongmanbot.config;

import com.github.philippheuer.events4j.core.EventManager;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.nicklastrange.strongmanbot.twitch.util.TwitchEmbedCreator;
import com.nicklastrange.strongmanbot.util.SystemUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class TwitchConfig {

    private final ApplicationContext ctx;

    public TwitchConfig(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    @Bean
    public TwitchClient twitchClientBuilder() {
        String TWITCH_CLIENT_ID = SystemUtil.getenv("TWITCH_CLIENT_ID");
        String TWITCH_CLIENT_SECRET = SystemUtil.getenv("TWITCH_CLIENT_SECRET");

        TwitchClient twitchClient = TwitchClientBuilder.builder()
                .withClientId(TWITCH_CLIENT_ID)
                .withClientSecret(TWITCH_CLIENT_SECRET)
                .withEnableHelix(true)
                .withDefaultEventHandler(SimpleEventHandler.class)
                .build();

        EventManager eventManager = twitchClient.getEventManager();
        eventManager.onEvent(ChannelGoLiveEvent.class, event -> TwitchEmbedCreator.createStreamGoLiveEmbed(event, twitchClient, ctx));
        return twitchClient;
    }
}
