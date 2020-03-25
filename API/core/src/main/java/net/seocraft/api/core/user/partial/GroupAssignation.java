package net.seocraft.api.core.user.partial;

import net.seocraft.api.core.group.Group;
import org.jetbrains.annotations.NotNull;

public interface GroupAssignation {

    @NotNull Group getGroup();

    @NotNull String getJoined();

    @NotNull String getComment();

}
