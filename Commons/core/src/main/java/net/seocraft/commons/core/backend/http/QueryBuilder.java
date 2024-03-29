package net.seocraft.commons.core.backend.http;

import com.google.inject.Inject;
import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class QueryBuilder {

    private HttpConfig config;

    @Inject protected QueryBuilder(HttpConfig config) {
        this.config = config;
    }

    URI getDefaultURI(String url, Map<String, String> params, boolean epsilon) {
        return getURI(url, params, epsilon, this.config.getHost());
    }

    URI getURI(String url, Map<String, String> params, boolean epsilon, String host) {
        try {
            URIBuilder uri = new URIBuilder()
                    .setScheme("https")
                    .setHost(host)
                    //.setPort(this.config.getPort())
                    .setPath(this.config.getSuffix() + "/" + url);
            if (epsilon) uri.setHost(this.config.getEpsilon());
            for (Map.Entry<String, String> entry: params.entrySet()) {
                uri.setParameter(entry.getKey(), entry.getValue());
            }
            return uri.build();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }
}
