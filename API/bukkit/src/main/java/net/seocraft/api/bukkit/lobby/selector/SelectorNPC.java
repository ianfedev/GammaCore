package net.seocraft.api.bukkit.lobby.selector;

import net.seocraft.api.bukkit.game.gamemode.Gamemode;
import net.seocraft.api.bukkit.game.gamemode.SubGamemode;
import org.jetbrains.annotations.NotNull;

public interface SelectorNPC {

    @NotNull Gamemode getGamemode();

    @NotNull SubGamemode getSubGamemode();

    @NotNull String getSkin();

    float getX();

    float getY();

    float getZ();

    float getYaw();

    float getPitch();

}
