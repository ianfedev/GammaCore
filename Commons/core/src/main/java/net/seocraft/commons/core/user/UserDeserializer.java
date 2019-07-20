package net.seocraft.commons.core.user;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.seocraft.api.core.group.Group;
import net.seocraft.api.core.old.model.PermissionGroup;
import net.seocraft.api.core.old.serialization.model.Deserializer;
import net.seocraft.api.core.user.User;
import net.seocraft.commons.core.backend.user.model.UserImp;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class UserDeserializer implements Deserializer<User> {

    @Inject Gson gson;
    @Inject JsonUtils parser;

    public User deserializeModel(String json) {
        JsonObject object = this.parser.parseObject(json);
        User user = gson.fromJson(object, UserImp.class);
        List<Group> parsedGroups = new ArrayList<>();
        JsonArray groups = object.get("group").getAsJsonArray();
        groups.forEach(group -> parsedGroups.add(gson.fromJson(group.getAsJsonObject().get("_id").getAsJsonObject(), PermissionGroup.class)));
        user.setGroups(parsedGroups);
        return user;
    }
}
