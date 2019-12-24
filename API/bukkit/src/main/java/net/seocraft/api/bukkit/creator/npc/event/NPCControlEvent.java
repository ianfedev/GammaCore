package net.seocraft.api.bukkit.creator.npc.event;

import lombok.Getter;
import lombok.Setter;
import net.seocraft.api.bukkit.creator.npc.NPC;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class NPCControlEvent extends NPCEvent implements Cancellable {

    private final HandlerList handlers = new HandlerList();
    @Setter private boolean cancelled = false;

    private Player player;
    private float sidewaysMotion;
    private float forwardMotion;

    public NPCControlEvent(@NotNull NPC npc, @NotNull Player player, float sidewaysMotion, float forwardMotion) {
        super(npc);
        this.player = player;
        this.sidewaysMotion = sidewaysMotion;
        this.forwardMotion = forwardMotion;
    }
}