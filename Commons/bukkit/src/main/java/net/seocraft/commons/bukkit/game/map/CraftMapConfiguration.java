package net.seocraft.commons.bukkit.game.map;

import net.seocraft.api.bukkit.game.map.BaseMapConfiguration;
import net.seocraft.api.bukkit.game.map.partial.Contribution;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class CraftMapConfiguration implements BaseMapConfiguration {

    @NotNull private String name;
    @NotNull private String author;
    @NotNull private String version;
    @NotNull private Set<Contribution> contributors;
    @NotNull private String gamemode;
    @NotNull private String subGamemode;
    @NotNull private String description;

    public CraftMapConfiguration(@NotNull String name, @NotNull String author, @NotNull String version, @NotNull Set<Contribution> contributors, @NotNull String gamemode, @NotNull String subGamemode, @NotNull String description) {
        this.name = name;
        this.author = author;
        this.version = version;
        this.contributors = contributors;
        this.gamemode = gamemode;
        this.subGamemode = subGamemode;
        this.description = description;
    }

    @Override
    public @NotNull String getName() {
        return this.name;
    }

    @Override
    public @NotNull String getAuthor() {
        return this.author;
    }

    @Override
    public @NotNull String getVersion() {
        return this.version;
    }

    @Override
    public @NotNull Set<Contribution> getContributors() {
        return this.contributors;
    }

    @Override
    public @NotNull String getGamemode() {
        return this.gamemode;
    }

    @Override
    public @NotNull String getSubGamemode() {
        return this.subGamemode;
    }

    @Override
    public @NotNull String getDescription() {
        return this.description;
    }

}
