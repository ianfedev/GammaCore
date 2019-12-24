package net.seocraft.api.bukkit.creator.npc.event;

import lombok.Getter;
import net.seocraft.api.bukkit.creator.npc.NPC;
import net.seocraft.api.bukkit.creator.npc.navigation.Path;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class NPCPathFinishEvent extends NPCEvent {

    private final HandlerList handlers = new HandlerList();

    @NotNull private Path path;

    public NPCPathFinishEvent(@NotNull NPC npc, @NotNull Path path) {
        super(npc);
        this.path = path;
    }
}