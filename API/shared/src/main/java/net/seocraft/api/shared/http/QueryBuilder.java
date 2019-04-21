package net.seocraft.api.shared.http;

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

    URI getURI(String url, Map<String, String> params) {
        try {
            URIBuilder uri = new URIBuilder()
                    .setScheme("http")
                    .setHost(this.config.getHost())
                    .setPort(this.config.getPort())
                    .setPath(this.config.getSuffix() + "/" + url);
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
