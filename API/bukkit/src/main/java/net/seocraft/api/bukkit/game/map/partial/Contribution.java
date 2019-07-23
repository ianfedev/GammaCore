package net.seocraft.api.bukkit.game.map.partial;

import org.jetbrains.annotations.NotNull;

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
