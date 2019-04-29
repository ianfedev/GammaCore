package net.seocraft.api.shared.models;

import com.google.gson.annotations.SerializedName;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.UUID;

@Getter @Setter
public class Group implements Model {

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private String id = UUID.randomUUID().toString();
    private String name;
    private int priority;
    @SerializedName("minecraft_flairs") private Set<MinecraftFlair> minecraftFlairs;
    @SerializedName("minecraft_permissions") private Set<String> permissions;
    private boolean staff;

    public String id() {
        return id;
    }

    @Getter @Setter
    private class MinecraftFlair {
        private String realm;
        private String color;
        private String symbol;
    }
}
