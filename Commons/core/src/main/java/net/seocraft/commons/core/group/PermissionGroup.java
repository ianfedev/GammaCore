package net.seocraft.commons.core.group;

import net.seocraft.api.core.group.Group;
import net.seocraft.api.core.group.partial.Flair;
import org.jetbrains.annotations.NotNull;

import java.beans.ConstructorProperties;
import java.util.Set;

public class PermissionGroup implements Group {

    @NotNull private String id;
    @NotNull private String name;
    private int priority;
    @NotNull private Set<Flair> flairs;
    @NotNull private Set<String> permissions;
    private boolean staff;

    @ConstructorProperties({"_id", "name", "priority", "minecraft_flair", "minecraft_permissions", "staff"})
    public PermissionGroup(@NotNull String id, @NotNull String name, int priority, @NotNull Set<Flair> flairs, @NotNull Set<String> permissions, boolean staff) {
        this.id = id;
        this.name = name;
        this.priority = priority;
        this.flairs = flairs;
        this.permissions = permissions;
        this.staff = staff;
    }

    @Override
    public @NotNull String getId() {
        return id;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public @NotNull Set<Flair> getMinecraftFlairs() {
        return flairs;
    }

    @Override
    public @NotNull Set<String> getPermissions() {
        return permissions;
    }

    @Override
    public boolean isStaff() {
        return staff;
    }
}
