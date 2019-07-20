package net.seocraft.api.bukkit.game.gamemode;

import net.seocraft.api.bukkit.old.game.gamemode.model.GamemodeImp;
import net.seocraft.api.core.storage.Model;
import net.seocraft.api.core.old.serialization.model.FieldName;
import net.seocraft.api.core.old.serialization.model.IgnoreMethod;
import net.seocraft.api.core.old.serialization.model.ImplementedBy;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

@ImplementedBy(GamemodeImp.class)
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
    @FieldName("lobby")
    @NotNull String getLobbyGroup();

    /**
     * @see ItemStack
     * @return Game navigator icon to be parsed.
     */
    @FieldName("navigator")
    @NotNull String getNavigatorIcon();

    /**
     * @return Name of the Gamemode.
     */
    @FieldName("slot")
    int getNavigatorSlot();

    /**
     * @see SubGamemode
     * @return Set of SubGamemodes which can be played.
     **/
    @FieldName("sub_types")
    @NotNull Set<SubGamemode> getSubGamemodes();

    /**
     * @return ItemStack to be used at navigators.
     */
    @IgnoreMethod
    @NotNull ItemStack obtainStackItem();
}
