package net.seocraft.api.bukkit.creator.npc.event;

import lombok.Getter;
import net.seocraft.api.bukkit.creator.npc.NPC;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class NPCDespawnEvent extends NPCEvent {

    private final HandlerList handlers = new HandlerList();

    public NPCDespawnEvent(@NotNull NPC npc) {
        super(npc);
    }
}