package net.seocraft.api.shared.http;

import java.util.Map;

public interface IHttpRequest {

    HttpType getType();

    String getURL();

    Map<String, String> getHeaders();

    Map<String, String> getQueryStrings();

    String getJSONParams();

}