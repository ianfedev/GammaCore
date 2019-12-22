package net.seocraft.api.bukkit.lobby.selector;

import net.seocraft.api.bukkit.game.gamemode.Gamemode;
import net.seocraft.api.bukkit.game.gamemode.SubGamemode;
import net.seocraft.creator.npc.NPCManager;
import net.seocraft.creator.skin.SkinProperty;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface SelectorNPC {

    @NotNull Gamemode getGamemode();

    @Nullable SubGamemode getSubGamemode();

    @NotNull SkinProperty getSkin();

    float getX();

    float getY();

    float getZ();

    float getYaw();

    float getPitch();

    void create(@NotNull Plugin plugin, @NotNull String name, @NotNull NPCManager manager);

}
