CREATE TABLE IF NOT EXISTS server (
    id bigserial PRIMARY KEY,
    server_id bigint NOT NULL UNIQUE,
    server_name text NOT NULL,
    server_prefix text NOT NULL DEFAULT '!',
    image_only_channels text[] NOT NULL DEFAULT '{}',
    is_twitch_notification_on bool NOT NULL DEFAULT false,
    twitch_notification_channel text NOT NULL DEFAULT '',
    twitch_streamers_to_notify text[] NOT NULL DEFAULT '{}'
);