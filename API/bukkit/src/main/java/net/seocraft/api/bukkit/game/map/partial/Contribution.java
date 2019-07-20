package net.seocraft.api.bukkit.game.map.partial;

import net.seocraft.api.bukkit.old.game.map.model.contribution.ContributionImp;
import net.seocraft.api.core.old.serialization.model.ImplementedBy;
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
