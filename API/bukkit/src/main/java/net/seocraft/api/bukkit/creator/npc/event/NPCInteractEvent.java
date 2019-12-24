package net.seocraft.api.bukkit.creator.npc.event;

import lombok.Getter;
import lombok.Setter;
import net.seocraft.api.bukkit.creator.npc.NPC;
import net.seocraft.api.bukkit.creator.npc.action.ClickType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class NPCInteractEvent extends NPCEvent implements Cancellable {

    private final HandlerList handlers = new HandlerList();
    @Setter private boolean cancelled = false;

    @NotNull private Player player;
    @NotNull private ClickType clickType;

    public NPCInteractEvent(@NotNull NPC npc, @NotNull Player player, @NotNull ClickType clickType) {
        super(npc);
        this.player = player;
        this.clickType = clickType;
    }
}