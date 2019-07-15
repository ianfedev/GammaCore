package net.seocraft.api.bukkit.game.map.model.rating;

import net.seocraft.api.shared.serialization.model.FieldName;
import net.seocraft.api.shared.serialization.model.ImplementedBy;
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
     * @see net.seocraft.api.shared.user.model.User
     * @return ID of who rated the map.
     */
    @FieldName("user")
    @NotNull String getUser();

}
