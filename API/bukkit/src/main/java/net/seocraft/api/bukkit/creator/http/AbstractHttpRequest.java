package net.seocraft.api.bukkit.creator.http;

import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractHttpRequest implements HttpRequest {

    private final CloseableHttpClient client = HttpClients.createDefault();

    public @NotNull Map<String, String> getHeaders() {
        return new HashMap<>();
    }

    public @NotNull String getJSONParams() {
        return "";
    }

    protected String request(@NotNull String requestWebsite) throws BadRequest, Unauthorized, NotFound, InternalServerError, MalformedURLException, URISyntaxException {
        String response = "";
        URI url = new URL(requestWebsite).toURI();
        ResponseHandler<String> handler = handleResponse();
        HttpResponse httpResponse = null;
        try {
            StringEntity entity = new StringEntity(getJSONParams(), ContentType.APPLICATION_JSON);
            switch (getType()) {
                case POST:
                    HttpPost post = new HttpPost(url);
                    getHeaders().forEach(post::setHeader);
                    post.setEntity(entity);
                    httpResponse = client.execute(post);
                    response = handler.handleResponse(httpResponse);
                    break;
                case DELETE:
                    HttpDelete delete = new HttpDelete(url);
                    getHeaders().forEach(delete::setHeader);
                    httpResponse = client.execute(delete);
                    response = handler.handleResponse(httpResponse);
                    break;
                case PUT:
                    HttpPut put = new HttpPut(url);
                    getHeaders().forEach(put::setHeader);
                    put.setEntity(entity);
                    httpResponse = client.execute(put);
                    response = handler.handleResponse(httpResponse);
                    break;
                case GET:
                    HttpGet get = new HttpGet(url);
                    getHeaders().forEach(get::setHeader);
                    httpResponse = client.execute(get);
                    response = handler.handleResponse(httpResponse);
                    break;
            }
            httpResponse.getStatusLine().getReasonPhrase();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        switch (httpResponse.getStatusLine().getStatusCode()) {
            default: break;
            case 400: throw new BadRequest(response);
            case 403: throw new Unauthorized(response);
            case 404: throw new NotFound(response);
            case 500: throw new InternalServerError(response);
        }
        return response;
    }

    private ResponseHandler<String> handleResponse() {
        return response -> {
            HttpEntity entity = response.getEntity();
            return entity != null ? EntityUtils.toString(entity) : null;
        };
    }
}