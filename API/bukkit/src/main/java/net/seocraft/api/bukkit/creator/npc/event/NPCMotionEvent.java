package net.seocraft.api.bukkit.creator.npc.event;

import lombok.Getter;
import lombok.Setter;
import net.seocraft.api.bukkit.creator.npc.NPC;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;


@Getter
public class NPCMotionEvent extends NPCEvent implements Cancellable {

    private final HandlerList handlers = new HandlerList();
    @Setter private boolean cancelled = false;

    @NotNull private Vector motion;

    public NPCMotionEvent(@NotNull NPC npc, @NotNull Vector motion) {
        super(npc);
        this.motion = motion;
    }
}