package net.seocraft.commons.bukkit.game.map.partial;

import net.seocraft.api.bukkit.game.map.partial.GameRating;
import net.seocraft.api.bukkit.game.map.partial.Rating;
import org.jetbrains.annotations.NotNull;

public class MapRating implements Rating {

    private GameRating rating;
    private String user;

    public MapRating(GameRating rating, String user) {
        this.rating = rating;
        this.user = user;
    }

    @Override
    public @NotNull GameRating getRating() {
        return this.rating;
    }

    @Override
    public @NotNull String getUser() {
        return this.user;
    }
}
