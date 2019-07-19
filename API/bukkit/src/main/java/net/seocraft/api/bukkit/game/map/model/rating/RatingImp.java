package net.seocraft.api.bukkit.game.map.model.rating;

import org.jetbrains.annotations.NotNull;

public class RatingImp implements Rating {

    private GameRating rating;
    private String user;

    public RatingImp(GameRating rating, String user) {
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
