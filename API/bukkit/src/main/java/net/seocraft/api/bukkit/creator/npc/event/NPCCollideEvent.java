package net.seocraft.api.bukkit.creator.npc.event;

import lombok.Getter;
import lombok.Setter;
import net.seocraft.api.bukkit.creator.npc.NPC;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class NPCCollideEvent extends NPCEvent implements Cancellable {

    private final HandlerList handlers = new HandlerList();
    @Setter private boolean cancelled = false;

    private Entity collidedEntity;

    public NPCCollideEvent(@NotNull NPC npc, Entity collidedEntity) {
        super(npc);
        this.collidedEntity = collidedEntity;
    }

}