package net.seocraft.api.bukkit.lobby.selector;

import net.seocraft.api.bukkit.game.gamemode.Gamemode;
import net.seocraft.api.bukkit.game.gamemode.SubGamemode;
import net.seocraft.creator.npc.NPC;
import net.seocraft.creator.npc.NPCManager;
import net.seocraft.creator.skin.SkinProperty;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface SelectorNPC {

    @NotNull Gamemode getGamemode();

    @Nullable SubGamemode getSubGamemode();

    @NotNull SkinProperty getSkin();

    double getX();

    double getY();

    double getZ();

    double getYaw();

    double getPitch();

    boolean isPerk();

    @Nullable NPC create(@NotNull Plugin plugin, @NotNull String name, @NotNull NPCManager manager);

}
