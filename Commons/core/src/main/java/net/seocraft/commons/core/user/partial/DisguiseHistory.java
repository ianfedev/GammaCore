package net.seocraft.commons.core.user.partial;

import net.seocraft.api.core.group.Group;
import net.seocraft.api.core.user.partial.Disguise;
import org.jetbrains.annotations.NotNull;

import java.beans.ConstructorProperties;

public class DisguiseHistory implements Disguise {

    @NotNull private String nickname;
    @NotNull private Group group;
    @NotNull private String createdAt;

    @ConstructorProperties({"nickname", "group", "created_at"})
    public DisguiseHistory(@NotNull String nickname, @NotNull Group group, @NotNull String createdAt) {
        this.nickname = nickname;
        this.group = group;
        this.createdAt = createdAt;
    }

    @Override
    public @NotNull String getNickname() {
        return nickname;
    }

    @Override
    public @NotNull Group getGroup() {
        return group;
    }

    @Override
    public @NotNull String getCreatedAt() {
        return createdAt;
    }
}