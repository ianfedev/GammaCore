package net.seocraft.api.bukkit.creator.npc.event;

import lombok.Getter;
import net.seocraft.api.bukkit.creator.npc.NPC;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class NPCSpawnEvent extends NPCEvent {

    private final HandlerList handlers = new HandlerList();

    public NPCSpawnEvent(@NotNull NPC npc) {
        super(npc);
    }
}