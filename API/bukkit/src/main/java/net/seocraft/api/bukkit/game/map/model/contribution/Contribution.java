package net.seocraft.api.bukkit.game.map.model.contribution;

import net.seocraft.api.shared.serialization.model.ImplementedBy;
import org.jetbrains.annotations.NotNull;

@ImplementedBy(ContributionImp.class)
public interface Contribution {

    /**
     * @return ID of who made the contribution.
     */
    @NotNull String getContributor();

    /**
     * @return String with the contribution description.
     */
    @NotNull String getContribution();

}
