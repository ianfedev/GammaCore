package net.seocraft.api.bukkit.creator.npc.event;

import lombok.Getter;
import lombok.Setter;
import net.seocraft.api.bukkit.creator.npc.NPC;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public class NPCDamageEvent extends NPCEvent implements Cancellable {

    private final HandlerList handlers = new HandlerList();
    @Setter private boolean cancelled = false;

    @NotNull private EntityDamageEvent.DamageCause damageCause;
    @Nullable private Entity damager;
    private float damage;

    public NPCDamageEvent(@NotNull NPC npc, @NotNull EntityDamageEvent.DamageCause damageCause, @Nullable Entity damager, float damage) {
        super(npc);
        this.damageCause = damageCause;
        this.damager = damager;
        this.damage = damage;
    }
}