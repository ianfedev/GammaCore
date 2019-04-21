package net.seocraft.api.shared.http;

import com.google.inject.Singleton;
import lombok.Getter;

@Singleton @Getter
class HttpConfig {

    private final String host = "127.0.0.1";
    private final Integer port = 3800;
    private final String suffix = "api";
}
