package net.seocraft.api.bukkit.game.gamemode;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.seocraft.api.core.storage.Model;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface Gamemode extends Model {

    /**
     * @return Name of the Gamemode.
     */
    @NotNull String getName();

    /**
     * @return Get Cloud lobby group to be teleported.
     */
    @JsonProperty("lobby")
    @NotNull String getLobbyGroup();

    /**
     * @see ItemStack
     * @return Game navigator icon to be parsed.
     */
    @JsonProperty("navigator")
    @NotNull String getNavigatorIcon();

    /**
     * @return Name of the Gamemode.
     */
    @JsonProperty("slot")
    int getNavigatorSlot();

    /**
     * @see SubGamemode
     * @return Set of SubGamemodes which can be played.
     **/
    @JsonProperty("subTypes")
    @NotNull Set<SubGamemode> getSubGamemodes();

    /**
     * @return ItemStack to be used at navigators.
     */
    @JsonIgnore
    @NotNull ItemStack obtainStackItem();
}
