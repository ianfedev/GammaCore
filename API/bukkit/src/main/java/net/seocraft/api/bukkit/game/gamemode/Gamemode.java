package net.seocraft.api.bukkit.game.gamemode;

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
     * @return Scoreboard lobby title of the Gamemode.
     */
    @NotNull String getScoreboard();

    /**
     * @return Get Cloud lobby group to be teleported.
     */
    @NotNull String getLobbyGroup();

    /**
     * @see ItemStack
     * @return Game navigator icon to be parsed.
     */
    @NotNull String getNavigatorIcon();

    /**
     * @return Name of the Gamemode.
     */
    int getNavigatorSlot();

    /**
     * @see SubGamemode
     * @return Set of SubGamemodes which can be played.
     **/
    @NotNull Set<SubGamemode> getSubGamemodes();

    /**
     * @return ItemStack to be used at navigators.
     */
    @NotNull ItemStack obtainStackItem();
}
