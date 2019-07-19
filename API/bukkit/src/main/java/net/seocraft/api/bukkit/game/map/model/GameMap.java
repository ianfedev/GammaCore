package net.seocraft.api.bukkit.game.map.model;

import net.seocraft.api.bukkit.game.map.model.contribution.Contribution;
import net.seocraft.api.bukkit.game.map.model.rating.Rating;
import net.seocraft.api.shared.model.Model;
import net.seocraft.api.shared.serialization.model.ImplementedBy;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

@ImplementedBy(GameMapImp.class)
public interface GameMap extends Model {

    /**
     * @return Name of the Map.
     */
    @NotNull String getName();

    /**
     * @return Base64 encoded zip file.
     */
    @NotNull String getFile();

    /**
     * @return JSON encoded configuration.
     */
    @NotNull String getConfiguration();

    /**
     * @return Base64 encoded image file.
     */
    @NotNull String getImage();

    /**
     * @see net.seocraft.api.shared.user.model.User
     * @return Map author's ID.
     */
    @NotNull String getAuthor();

    /**
     * @return Actual version of the map.
     */
    @NotNull String getVersion();

    /**
     * @see Contribution
     * @return Contribution set of the map.
     */
    @NotNull Set<Contribution> getContributors();

    /**
     * @see net.seocraft.api.bukkit.game.gamemode.model.Gamemode
     * @return ID of the map's Gamemode.
     */
    @NotNull String getGamemode();

    /**
     * @see net.seocraft.api.bukkit.game.subgame.SubGamemode
     * @return ID of the map's SubGamemode.
     */
    @NotNull String getSubGamemode();

    /**
     * @return Description of the map.
     */
    @NotNull String getDescription();

    /**
     * @return Ratings set of the map.
     */
    @NotNull Set<Rating> getRating();

    /**
     * @return UNIX Timestamp with registration day of the map.
     */
    long getRegisteredDate();

}
