package com.nicklastrange.strongmanbot.util;

import java.util.Optional;

public abstract class SystemUtil {

    public static String getenv(String key) {
        return Optional.ofNullable(System.getenv(key)).orElse("");
    }
}
