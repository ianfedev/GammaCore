package net.seocraft.api.bukkit.creator.npc.action;

import net.seocraft.api.bukkit.creator.npc.NPC;
import net.seocraft.api.bukkit.creator.npc.event.NPCEvent;
import org.jetbrains.annotations.NotNull;

public interface ActionHandler {

    void handle(@NotNull NPC npc, @NotNull NPCEvent npcEvent);

}