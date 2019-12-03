package net.seocraft.commons.core.backend.http;

import com.google.inject.Singleton;

@Singleton
class HttpConfig {

    private final String host = "api.seocraft.net";
    private final Integer port = 3800;
    private final String suffix = "api";

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }

    public String getSuffix() {
        return suffix;
    }
}
