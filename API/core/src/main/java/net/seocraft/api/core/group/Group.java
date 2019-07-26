package net.seocraft.api.core.group;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.seocraft.api.core.group.partial.Flair;
import net.seocraft.api.core.storage.Model;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface Group extends Model {

    @NotNull String getName();

    int getPriority();

    @JsonProperty("minecraft_flair")
    @NotNull Set<Flair> getMinecraftFlairs();

    @JsonProperty("minecraft_permissions")
    @NotNull Set<String> getPermissions();

    boolean isStaff();

}
