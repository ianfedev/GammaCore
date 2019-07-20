package net.seocraft.commons.core.backend.http;

import com.google.inject.Singleton;
import lombok.Getter;

@Singleton @Getter
class HttpConfig {

    private final String host = "167.86.74.233";
    private final Integer port = 3800;
    private final String suffix = "api";
}
