package net.seocraft.api.bukkit.game.map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.seocraft.api.bukkit.game.gamemode.Gamemode;
import net.seocraft.api.bukkit.game.gamemode.SubGamemode;
import net.seocraft.api.bukkit.game.map.partial.Contribution;
import net.seocraft.api.bukkit.game.map.partial.Rating;
import net.seocraft.api.core.storage.Model;
import net.seocraft.api.core.user.User;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

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
     * @see User
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
     * @see Gamemode
     * @return ID of the map's Gamemode.
     */
    @NotNull String getGamemode();

    /**
     * @see SubGamemode
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

    @JsonIgnore
    void setMapJSON(@NotNull String json);

}
