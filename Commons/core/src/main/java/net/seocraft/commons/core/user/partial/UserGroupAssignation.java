package net.seocraft.commons.core.user.partial;

import net.seocraft.api.core.group.Group;
import net.seocraft.api.core.user.partial.GroupAssignation;
import org.jetbrains.annotations.NotNull;

import java.beans.ConstructorProperties;

public class UserGroupAssignation implements GroupAssignation {

    @NotNull private Group group;
    @NotNull private String joined;
    @NotNull private String comment;

    @ConstructorProperties({
            "group",
            "joined",
            "comment"
    })
    public UserGroupAssignation(@NotNull Group group, @NotNull String joined, @NotNull String comment) {
        this.group = group;
        this.joined = joined;
        this.comment = comment;
    }

    @Override
    public @NotNull Group getGroup() {
        return this.group;
    }

    @Override
    public @NotNull String getJoined() {
        return this.joined;
    }

    @Override
    public @NotNull String getComment() {
        return this.comment;
    }
}
