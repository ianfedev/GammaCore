package net.seocraft.api.bukkit.creator.npc.entity.player;

import net.seocraft.api.bukkit.creator.npc.NPC;
import net.seocraft.api.bukkit.creator.skin.SkinLayer;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface NPCPlayer extends NPC {

    @Override
    @NotNull Player getBukkitEntity();

    @NotNull GameMode getGamemode();

    @NotNull SkinLayer[] getActiveSkinLayers();

    int getPing();

    boolean isLying();

    boolean isShownInList();

    void setGamemode(@NotNull GameMode gamemode);

    void setSkinLayers(@NotNull SkinLayer... layers) throws NoSuchFieldException, IllegalAccessException;

    void setPing(int ping);

    void setLying(boolean lying);

    void setShownInList(boolean shownInList);

}