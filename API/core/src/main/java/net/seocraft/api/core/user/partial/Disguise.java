package net.seocraft.api.core.user.partial;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.seocraft.api.core.group.Group;
import org.jetbrains.annotations.NotNull;

public interface Disguise {

    @NotNull String getNickname();

    @NotNull Group getGroup();

    @JsonProperty("created_at")
    @NotNull String getCreatedAt();

}
