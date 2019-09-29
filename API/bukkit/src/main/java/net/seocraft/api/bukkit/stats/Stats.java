package net.seocraft.api.bukkit.stats;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.seocraft.api.bukkit.stats.skywars.SkyWarsStats;
import net.seocraft.api.core.storage.Model;
import org.jetbrains.annotations.NotNull;

public interface Stats extends Model {

    @JsonProperty("username")
    @NotNull String getOwner();

    @JsonProperty("skyWars")
    @NotNull SkyWarsStats getSkyWarsStats();

}
