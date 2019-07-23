package net.seocraft.api.core.group;

import net.seocraft.api.core.group.partial.Flair;
import net.seocraft.api.core.storage.Model;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface Group extends Model {

    @NotNull String getName();

    int getPriority();

    @NotNull Set<Flair> getMinecraftFlairs();

    @NotNull Set<String> getPermissions();

    boolean isStaff();

}
