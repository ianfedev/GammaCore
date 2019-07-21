package net.seocraft.api.bukkit.game.map.partial;

import net.seocraft.api.core.user.User;
import org.jetbrains.annotations.NotNull;

public interface Rating {

    /**
     * @see GameRating
     * @return GameRating of the map.
     */
    @NotNull GameRating getRating();

    /**
     * @see User
     * @return ID of who rated the map.
     */
    @NotNull String getUser();

}
