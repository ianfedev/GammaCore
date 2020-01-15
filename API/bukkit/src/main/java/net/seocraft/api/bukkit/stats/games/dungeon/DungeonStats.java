package net.seocraft.api.bukkit.stats.games.dungeon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface DungeonStats {

    int getSquamas();

    void addSquamas(int squamas);

    void removeSquamas(int squamas);

    int getCrowns();

    void addCrowns(int crowns);

    void removeCrowns(int crowns);

    double getExperience();

    void addExperience(double experience);

    int getLevel();

    @NotNull DungeonKit getHelmet();

    void setHelmet(@NotNull DungeonKit kit);

    @NotNull DungeonKit getChestplate();

    void setChestplate(@NotNull DungeonKit kit);

    @NotNull DungeonKit getLeggings();

    void setLeggings(@NotNull DungeonKit kit);

    @NotNull DungeonKit getBoots();

    void setBoots(@NotNull DungeonKit kit);

    @NotNull DungeonKit getSword();

    void setSword(@NotNull DungeonKit kit);

    @NotNull DungeonKit getBow();

    void setBow(@NotNull DungeonKit kit);

}
