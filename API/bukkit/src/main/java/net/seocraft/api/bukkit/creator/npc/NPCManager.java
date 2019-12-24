package net.seocraft.api.bukkit.creator.npc;

import net.seocraft.api.bukkit.creator.npc.entity.player.NPCPlayer;
import net.seocraft.api.bukkit.creator.skin.SkinProperty;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public interface NPCManager {

    @NotNull List<NPC> getNpcs();

    @NotNull NPCPlayer createPlayerNPC(@NotNull Plugin plugin, @NotNull Location location, @NotNull String name, @NotNull SkinProperty skinProperty);

    @NotNull Optional<NPC> getNPC(@NotNull String name);

    @NotNull Optional<NPC> getNPC(@NotNull Entity entity);

    boolean isNpc(@NotNull Entity entity);

}