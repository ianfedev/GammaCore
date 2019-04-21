package net.seocraft.api.shared.serialization;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.inject.Inject;

public class JsonUtils {

    private JsonParser parser;
    private Gson gson;

    @Inject JsonUtils(Gson gson, JsonParser parser) {
        this.parser = parser;
        this.gson = gson;
    }

    public String errorContext(String error) {
        JsonElement message = this.parser.parse(error);
        JsonObject object = message.getAsJsonObject();
        return object.get("message").getAsString();
    }

    public JsonElement parseJson(String raw, String property) {
        JsonElement element = this.parser.parse(raw);
        JsonObject object = element.getAsJsonObject();
        return object.get(property);
    }

    public JsonObject parseObject(String raw, String object) {
        return this.parser.parse(raw)
                .getAsJsonObject()
                .get(object)
                .getAsJsonObject();
    }

    public JsonObject parseObject(String raw) {
        return this.parser.parse(raw).getAsJsonObject();
    }

    public String encode(JsonObject object) {
        return this.gson.toJson(object);
    }
}
