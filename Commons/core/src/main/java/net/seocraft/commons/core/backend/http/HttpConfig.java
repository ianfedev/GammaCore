package net.seocraft.commons.core.backend.http;

import com.google.inject.Singleton;

@Singleton
class HttpConfig {

    private final String host = "api.seocraft.net";
    private final String epsilon = "epsilon.seocraft.net";
    private final String suffix = "api";

    public String getHost() {
        return host;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getEpsilon() {
        return epsilon;
    }
}
