package net.seocraft.commons.bukkit.game.map.partial;

import net.seocraft.api.bukkit.game.map.partial.Contribution;
import org.jetbrains.annotations.NotNull;

public class MapContribution implements Contribution {

    @NotNull private String contributor;
    @NotNull private String contribution;

    public MapContribution(@NotNull String contributor, @NotNull String contribution) {
        this.contributor = contributor;
        this.contribution = contribution;
    }

    @Override
    public @NotNull String getContributor() {
        return this.contributor;
    }

    @Override
    public @NotNull String getContribution() {
        return this.contribution;
    }
}
