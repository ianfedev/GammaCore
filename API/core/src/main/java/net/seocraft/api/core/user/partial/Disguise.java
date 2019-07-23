package net.seocraft.api.core.user.partial;

import net.seocraft.api.core.group.Group;
import org.jetbrains.annotations.NotNull;

public interface Disguise {

    @NotNull String getNickname();

    @NotNull Group getGroup();

    @NotNull String getCreatedAt();

}
