package net.seocraft.commons.bukkit.game.map;

import net.seocraft.api.bukkit.game.map.partial.Contribution;
import net.seocraft.api.bukkit.game.map.GameMap;
import net.seocraft.api.bukkit.game.map.partial.Rating;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class CoreMap implements GameMap {

    @NotNull private String id;
    @NotNull private String name;
    @NotNull private String file;
    @NotNull private String configuration;
    @NotNull private String image;
    @NotNull private String author;
    @NotNull private String version;
    @NotNull private Set<Contribution> contributors;
    @NotNull private String gamemode;
    @NotNull private String subGamemode;
    @NotNull private String description;
    @NotNull private Set<Rating> rating;
    private long registeredDate;

    public CoreMap(@NotNull String id, @NotNull String name, @NotNull String file, @NotNull String configuration, @NotNull String image, @NotNull String author, @NotNull String version, @NotNull Set<Contribution> contributors, @NotNull String gamemode, @NotNull String subGamemode, @NotNull String description, @NotNull Set<Rating> rating, long registeredDate) {
        this.id = id;
        this.name = name;
        this.file = file;
        this.configuration = configuration;
        this.image = image;
        this.author = author;
        this.version = version;
        this.contributors = contributors;
        this.gamemode = gamemode;
        this.subGamemode = subGamemode;
        this.description = description;
        this.rating = rating;
        this.registeredDate = registeredDate;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public @NotNull String getName() {
        return this.name;
    }

    @Override
    public @NotNull String getFile() {
        return this.file;
    }

    @Override
    public @NotNull String getConfiguration() {
        return this.configuration;
    }

    @Override
    public @NotNull String getImage() {
        return this.image;
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

    @Override
    public @NotNull Set<Rating> getRating() {
        return this.rating;
    }

    @Override
    public long getRegisteredDate() {
        return this.registeredDate;
    }

}
