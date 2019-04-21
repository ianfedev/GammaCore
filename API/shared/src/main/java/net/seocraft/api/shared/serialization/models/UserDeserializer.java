package net.seocraft.api.shared.serialization.models;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.seocraft.api.shared.models.Group;
import net.seocraft.api.shared.models.User;
import net.seocraft.api.shared.serialization.JsonUtils;

import java.util.HashMap;

@Singleton
public class UserDeserializer implements Deserializer<User> {

    @Inject Gson gson;
    @Inject JsonUtils parser;

    public User deserializeModel(String json) {
        JsonObject object = this.parser.parseObject(json);
        User user = gson.fromJson(object, User.class);
        HashMap<String, Group> parsedGroups = new HashMap<>();
        JsonArray groups = object.get("group").getAsJsonArray();
        groups.forEach(group -> parsedGroups.put(group.getAsJsonObject().get("_id").getAsJsonObject().get("name").getAsString(), gson.fromJson(group.getAsJsonObject().get("_id").getAsJsonObject(), Group.class)));
        user.setGroups(parsedGroups);
        return user;
    }
}
