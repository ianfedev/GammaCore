package net.seocraft.api.bukkit.creator.npc.event;

import lombok.Getter;
import net.seocraft.api.bukkit.creator.npc.NPC;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

@Getter
public abstract class NPCEvent extends Event {

    @NotNull private NPC npc;

    public NPCEvent(@NotNull NPC npc) {
        super();
        this.npc = npc;
    }
}