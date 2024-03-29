package net.seocraft.api.bukkit.stats;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.seocraft.api.bukkit.stats.games.SkyWarsStats;
import net.seocraft.api.bukkit.stats.games.TNTGamesStats;
import net.seocraft.api.bukkit.stats.games.dungeon.DungeonStats;
import net.seocraft.api.core.storage.Model;
import org.jetbrains.annotations.NotNull;

public interface Stats extends Model {

    @JsonProperty("username")
    @NotNull String getOwner();

    @JsonProperty("skyWars")
    @NotNull SkyWarsStats getSkyWarsStats();

    @JsonProperty("tntGames")
    @NotNull TNTGamesStats getTNTGamesStats();

    @JsonProperty("dungeon")
    @NotNull DungeonStats getDungeonStats();

}
