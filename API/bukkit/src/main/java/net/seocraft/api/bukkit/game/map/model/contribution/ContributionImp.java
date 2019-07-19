package net.seocraft.api.bukkit.game.map.model.contribution;

import org.jetbrains.annotations.NotNull;

public class ContributionImp implements Contribution {

    @NotNull private String contributor;
    @NotNull private String contribution;

    public ContributionImp(@NotNull String contributor, @NotNull String contribution) {
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
