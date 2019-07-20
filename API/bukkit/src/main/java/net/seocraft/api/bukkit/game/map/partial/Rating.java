package net.seocraft.api.bukkit.game.map.partial;

import net.seocraft.api.bukkit.old.game.map.model.rating.GameRating;
import net.seocraft.api.bukkit.old.game.map.model.rating.RatingImp;
import net.seocraft.api.core.old.serialization.model.FieldName;
import net.seocraft.api.core.old.serialization.model.ImplementedBy;
import net.seocraft.api.core.user.User;
import org.jetbrains.annotations.NotNull;

@ImplementedBy(RatingImp.class)
public interface Rating {

    /**
     * @see GameRating
     * @return GameRating of the map.
     */
    @FieldName("star")
    @NotNull GameRating getRating();

    /**
     * @see User
     * @return ID of who rated the map.
     */
    @FieldName("user")
    @NotNull String getUser();

}
