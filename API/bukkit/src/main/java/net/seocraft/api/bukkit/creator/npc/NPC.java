package net.seocraft.api.bukkit.creator.npc;

import net.seocraft.api.bukkit.creator.npc.action.ActionHandler;
import net.seocraft.api.bukkit.creator.npc.animation.Animation;
import net.seocraft.api.bukkit.creator.npc.equipment.EquipmentSlot;
import net.seocraft.api.bukkit.creator.npc.navigation.Navigator;
import net.seocraft.api.bukkit.creator.npc.navigation.Path;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NPC {

    int getEntityId();

    @NotNull UUID getUUID();

    @NotNull String getName();

    @NotNull Navigator getNavigator();

    @NotNull Optional<Path> getCurrentPath();

    @NotNull List<ActionHandler> getActionHandlers();

    @NotNull Location getLocation();

    @NotNull LivingEntity getBukkitEntity();

    @NotNull Optional<Entity> getPassenger();

    @NotNull Optional<Entity> getTarget();

    boolean isInvulnerable();

    boolean hasCollision();

    float getGravity();

    boolean hasGravity();

    boolean isFrozen();

    boolean isControllable();

    float getYaw();

    float getPitch();

    void despawn();

    boolean pathfindTo(@NotNull Location destination);

    boolean pathfindTo(@NotNull Location destination, double speed);

    boolean pathfindTo(@NotNull Location destination, double speed, double range);

    void teleport(@NotNull Location location);

    void setPassenger(@NotNull Entity passenger);

    void setTarget(@Nullable Entity target);

    void setInvulnerable(boolean invulnerable);

    void setCollision(boolean collision);

    void setGravity(float gravity);

    void setGravity(boolean gravity);

    void setFrozen(boolean frozen);

    void setControllable(boolean controllable);

    void setYaw(float yaw);

    void setPitch(float pitch);

    void lookAt(@NotNull Location location);

    void playAnimation(@NotNull Animation animation);

    void setEquipment(EquipmentSlot equipmentSlot, ItemStack itemStack);

    @NotNull Optional<ItemStack> getEquipment(EquipmentSlot equipmentSlot);

    default @NotNull NPC addActionHandler(@NotNull ActionHandler actionHandler) {
        this.getActionHandlers().add(actionHandler);
        return this;
    }

}