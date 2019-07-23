package net.seocraft.commons.core.backend.http;

import com.google.inject.Inject;
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

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public abstract class HttpRequest implements IHttpRequest {

    @Inject private QueryBuilder builder;
    private CloseableHttpClient client = HttpClients.createDefault();

    public Map<String, String> getHeaders() {
        return new HashMap<>();
    }

    public Map<String, String> getQueryStrings() {
        return new HashMap<>();
    }

    public String getJSONParams() {
        return "";
    }

    protected String getResponse() throws BadRequest, Unauthorized, NotFound, InternalServerError {
        String response = "";
        URI url = this.builder.getURI(getURL(), getQueryStrings());
        ResponseHandler<String> handler = handleResponse();
        HttpResponse http_response = null;
        try {
            StringEntity entity = new StringEntity(getJSONParams(), ContentType.APPLICATION_JSON);
            switch (getType()) {
                case POST:
                    HttpPost post = new HttpPost(url);
                    getHeaders().forEach(post::setHeader);
                    post.setEntity(entity);
                    http_response = client.execute(post);
                    response = handler.handleResponse(http_response);
                    break;
                case DELETE:
                    HttpDelete delete = new HttpDelete(url);
                    getHeaders().forEach(delete::setHeader);
                    http_response = client.execute(delete);
                    response = handler.handleResponse(http_response);
                    break;
                case PUT:
                    HttpPut put = new HttpPut(url);
                    getHeaders().forEach(put::setHeader);
                    put.setEntity(entity);
                    http_response = client.execute(put);
                    response = handler.handleResponse(http_response);
                    break;
                case GET:
                    HttpGet get = new HttpGet(url);
                    getHeaders().forEach(get::setHeader);
                    http_response = client.execute(get);
                    response = handler.handleResponse(http_response);
                    break;
            }

            http_response.getStatusLine().getReasonPhrase();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        switch (http_response.getStatusLine().getStatusCode()) {
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
