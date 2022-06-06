package com.nicklastrange.strongmanbot.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@With
@Table("server")
public class Server {
    @Id
    private Long id;
    @Column("server_id")
    private Long serverId;
    @Column("server_name")
    private String serverName;
    @Column("server_prefix")
    private String serverPrefix;
    @Column("image_only_channels")
    private Set<String> imageOnlyChannels = new HashSet<>();
    @Column("is_twitch_notification_on")
    private boolean isTwitchNotificationOn = false;
    @Column("twitch_notification_channel")
    private String twitchNotificationChannel = "";
    @Column("twitch_streamers_to_notify")
    private Set<String> twitchStreamersToNotify = new HashSet<>();

}
