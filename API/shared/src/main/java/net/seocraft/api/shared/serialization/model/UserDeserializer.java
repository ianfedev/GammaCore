package net.seocraft.api.shared.serialization.model;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.seocraft.api.shared.model.Group;
import net.seocraft.api.shared.model.User;
import net.seocraft.api.shared.serialization.JsonUtils;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class UserDeserializer implements Deserializer<User> {

    @Inject Gson gson;
    @Inject JsonUtils parser;

    public User deserializeModel(String json) {
        JsonObject object = this.parser.parseObject(json);
        User user = gson.fromJson(object, User.class);
        List<Group> parsedGroups = new ArrayList<>();
        JsonArray groups = object.get("group").getAsJsonArray();
        groups.forEach(group -> parsedGroups.add(gson.fromJson(group.getAsJsonObject().get("_id").getAsJsonObject(), Group.class)));
        user.setGroups(parsedGroups);
        return user;
    }
}
