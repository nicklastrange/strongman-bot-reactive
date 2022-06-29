package com.nicklastrange.strongmanbot.twitch.util;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.helix.domain.Game;
import com.github.twitch4j.helix.domain.Stream;
import com.github.twitch4j.helix.domain.User;
import com.nicklastrange.strongmanbot.service.ServerService;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import org.springframework.context.ApplicationContext;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Set;

public abstract class TwitchEmbedCreator {

    public static void createStreamGoLiveEmbed(ChannelGoLiveEvent event, TwitchClient client, ApplicationContext ctx) {
        ServerService serverService = ctx.getBean(ServerService.class);
        GatewayDiscordClient bot = ctx.getBean(GatewayDiscordClient.class);

        Stream stream = event.getStream();
        User streamUser = client.getHelix()
                .getUsers(null, null, Collections.singletonList(stream.getUserLogin()))
                .execute()
                .getUsers()
                .get(0);
        Game streamGame = client.getHelix()
                .getGames(null, null, Collections.singletonList(stream.getGameName()))
                .execute()
                .getGames()
                .get(0);

        bot.getGuilds()
                .flatMap(guild -> serverService.findServerByServerId(guild.getId().asLong())
                        .flatMap(server -> {
                            boolean isTwitchNotificationOn = server.isTwitchNotificationOn();
                            final String twitchNotificationChannel = server.getTwitchNotificationChannel();
                            final Set<String> twitchStreamersToNotify = server.getTwitchStreamersToNotify();
                            if (isTwitchNotificationOn && !twitchNotificationChannel.equals("") &&
                                    (twitchStreamersToNotify.contains(stream.getUserName()) || twitchStreamersToNotify.contains(stream.getUserName()))) {
                                EmbedCreateSpec embed = EmbedCreateSpec.builder()
                                        .author(stream.getUserName() + " is now live!","https://www.twitch.tv/" + stream.getUserLogin(), streamUser.getProfileImageUrl())
                                        .title(stream.getTitle())
                                        .image(stream.getThumbnailUrl(350,200))
                                        .addField("\uD83C\uDFAE Game: `"+stream.getGameName()+"`\n" +
                                                "⌚ Started: `" + stream.getUptime().getSeconds() + " seconds ago`\n" +
                                                "\uD83E\uDDD1\u200D\uD83E\uDD1D\u200D\uD83E\uDDD1 For: `" + stream.getViewerCount() + " viewers`", "", false)
                                        .thumbnail(streamGame.getBoxArtUrl(100,133))
                                        .color(Color.of(100,65,165))
                                        .footer("https://www.twitch.tv/" + stream.getUserLogin(), streamUser.getProfileImageUrl())
                                        .build();
                                return guild.getChannelById(Snowflake.of(twitchNotificationChannel))
                                        .ofType(GuildMessageChannel.class)
                                        .flatMap(channel -> channel.createMessage(embed));
                            }
                            return Mono.empty();
                        }))
                .subscribe();
    }
}
