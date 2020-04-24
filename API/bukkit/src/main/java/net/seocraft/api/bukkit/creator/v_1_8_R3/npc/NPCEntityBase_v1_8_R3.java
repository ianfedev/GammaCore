package net.seocraft.api.bukkit.creator.v_1_8_R3.npc;

import net.minecraft.server.v1_8_R3.DamageSource;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.PacketPlayOutAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment;
import net.seocraft.api.bukkit.creator.npc.NPC;
import net.seocraft.api.bukkit.creator.npc.action.ActionHandler;
import net.seocraft.api.bukkit.creator.npc.action.ClickType;
import net.seocraft.api.bukkit.creator.npc.animation.Animation;
import net.seocraft.api.bukkit.creator.npc.entity.NPCEntity;
import net.seocraft.api.bukkit.creator.npc.entity.player.NPCPlayer;
import net.seocraft.api.bukkit.creator.npc.equipment.EquipmentSlot;
import net.seocraft.api.bukkit.creator.npc.event.*;
import net.seocraft.api.bukkit.creator.npc.navigation.Navigator;
import net.seocraft.api.bukkit.creator.npc.navigation.Path;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class NPCEntityBase_v1_8_R3 implements NPC, NPCEntity {

    private Navigator navigator;

    protected Object minecraftServer;
    protected Object worldServer;

    protected String name;

    protected Object interactManager;

    protected Object entity;

    protected Object pathFinder;
    protected Path currentPath;


    private List<ActionHandler> actionHandlers = new ArrayList<>();

    private Entity target;

    private boolean invulnerable = true;
    private boolean collision = false;
    private boolean hasGravity = false;
    private boolean frozen = true;
    private boolean controllable = false;

    private float gravity = 0.15f;

    public NPCEntityBase_v1_8_R3(@NotNull Navigator navigator, @NotNull World world) {
        this.navigator = navigator;
        this.minecraftServer = ((CraftServer) Bukkit.getServer()).getServer();
        this.worldServer = ((CraftWorld) world).getHandle();
    }

    protected abstract void initialize(@NotNull World world, @NotNull Location location) throws Exception;

    @Override
    public int getEntityId() {
        return ((net.minecraft.server.v1_8_R3.Entity) this.entity).getId();
    }

    @Override
    public @NotNull String getName() {
        return this.name;
    }

    @Override
    public @NotNull Navigator getNavigator() {
        return this.navigator;
    }

    @Override
    public @NotNull Optional<Path> getCurrentPath() {
        return Optional.ofNullable(this.currentPath);
    }

    @Override
    public @NotNull List<ActionHandler> getActionHandlers() {
        return this.actionHandlers;
    }

    @Override
    public @NotNull Location getLocation() {

        return new Location(
                ((net.minecraft.server.v1_8_R3.Entity) this.entity).getWorld().getWorld(),
                ((net.minecraft.server.v1_8_R3.Entity) this.entity).locX,
                ((net.minecraft.server.v1_8_R3.Entity) this.entity).locY,
                ((net.minecraft.server.v1_8_R3.Entity) this.entity).locZ,
                ((net.minecraft.server.v1_8_R3.Entity) this.entity).yaw,
                ((net.minecraft.server.v1_8_R3.Entity) this.entity).pitch
        );
    }

    @Override
    public @NotNull LivingEntity getBukkitEntity() {
        return (LivingEntity) ((net.minecraft.server.v1_8_R3.Entity) this.entity).getBukkitEntity();
    }

    @Override
    public @NotNull Optional<Entity> getPassenger() {
        return Optional.ofNullable(this.getBukkitEntity().getPassenger());
    }

    @Override
    public @NotNull Optional<Entity> getTarget() {
        return Optional.ofNullable(this.target);
    }

    @Override
    public boolean isInvulnerable() {
        return this.invulnerable;
    }

    @Override
    public boolean hasCollision() {
        return this.collision;
    }

    @Override
    public float getGravity() {
        return this.gravity;
    }

    @Override
    public boolean hasGravity() {
        return this.hasGravity;
    }

    @Override
    public boolean isFrozen() {
        return this.frozen;
    }

    @Override
    public boolean isControllable() {
        return this.controllable;
    }

    @Override
    public float getYaw() {
        return ((net.minecraft.server.v1_8_R3.Entity) this.entity).yaw;
    }

    @Override
    public float getPitch() {
        return ((net.minecraft.server.v1_8_R3.Entity) this.entity).pitch;
    }

    @Override
    public void despawn() {
        this.getBukkitEntity().remove();
    }

    @Override
    public boolean pathfindTo(@NotNull Location destination) {
        return this.pathfindTo(destination, 0.2D);
    }

    @Override
    public boolean pathfindTo(@NotNull Location destination, double speed) {
        return this.pathfindTo(destination, speed, 30.0D);
    }

    @Override
    public boolean pathfindTo(@NotNull Location destination, double speed, double range) {
        if (isFrozen()) return false;
        Optional<Path> optionalPath = this.getNavigator().findPath(this, destination, speed, range);
        if (!optionalPath.isPresent()) return false;
        this.currentPath = optionalPath.get();
        return true;
    }

    @Override
    public void teleport(@NotNull Location location) {
        this.getBukkitEntity().teleport(location);
        this.setYaw(location.getYaw());
        this.setPitch(location.getPitch());
    }

    @Override
    public void setPassenger(@NotNull Entity passenger) {
        this.getBukkitEntity().setPassenger(passenger);
    }

    @Override
    public void setTarget(@Nullable Entity target) {
        this.target = target;
        if (target != null) {
            this.lookAt(target.getLocation());
        }
    }

    @Override
    public void setInvulnerable(boolean invulnerable) {
        this.invulnerable = invulnerable;
    }

    @Override
    public void setCollision(boolean collision) {
        this.collision = collision;
    }

    @Override
    public void setGravity(float gravity) {
        this.gravity = gravity;
    }

    @Override
    public void setGravity(boolean gravity) {
        this.hasGravity = gravity;
    }

    @Override
    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }

    @Override
    public void setControllable(boolean controllable) {
        this.controllable = controllable;
    }

    @Override
    public void setYaw(float yaw) {
        ((net.minecraft.server.v1_8_R3.Entity) this.entity).yaw = yaw;
    }

    @Override
    public void setPitch(float pitch) {
        ((net.minecraft.server.v1_8_R3.Entity) this.entity).pitch = pitch;
    }

    @Override
    public void lookAt(@NotNull Location location) {
        if (this.getLocation().getWorld() != location.getWorld()) return;
        double dx = location.getX() - this.getBukkitEntity().getEyeLocation().getX();
        double dy = location.getY() - this.getBukkitEntity().getEyeLocation().getY();
        double dz = location.getZ() - this.getBukkitEntity().getEyeLocation().getZ();
        double xzd = Math.sqrt(dx * dx + dz * dz);
        double yd = Math.sqrt(xzd * xzd + dy * dy);

        double yaw = Math.toDegrees(Math.atan2(dz, dx)) - 90.0D;
        if (yaw < 0) {
            yaw += 360;
        }
        double pitch = -Math.toDegrees(Math.atan2(dy, yd));

        this.setYaw((float) yaw);
        this.setPitch((float) pitch);

    }

    @Override
    public void playAnimation(@NotNull Animation animation) {
        PacketPlayOutAnimation packet = new PacketPlayOutAnimation((net.minecraft.server.v1_8_R3.Entity) this.entity, animation.getId());
        Bukkit.getOnlinePlayers().forEach(player -> ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet));
    }

    @Override
    public void setEquipment(EquipmentSlot equipmentSlot, ItemStack itemStack) {
        switch (equipmentSlot) {
            case HAND:
                this.getBukkitEntity().getEquipment().setItemInHand(itemStack);
                break;
            case FEET:
                this.getBukkitEntity().getEquipment().setBoots(itemStack);
                break;
            case LEGS:
                this.getBukkitEntity().getEquipment().setLeggings(itemStack);
                break;
            case CHEST:
                this.getBukkitEntity().getEquipment().setChestplate(itemStack);
                break;
            case HEAD:
                this.getBukkitEntity().getEquipment().setHelmet(itemStack);
                break;
        }

        net.minecraft.server.v1_8_R3.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment(getEntityId(), equipmentSlot.getId(), nmsItemStack);
        Bukkit.getOnlinePlayers().forEach(player -> ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet));
    }

    @Override
    public @NotNull Optional<ItemStack> getEquipment(EquipmentSlot equipmentSlot) {
        switch (equipmentSlot) {
            case HAND:
                return Optional.ofNullable(this.getBukkitEntity().getEquipment().getItemInHand());
            case FEET:
                return Optional.ofNullable(this.getBukkitEntity().getEquipment().getBoots());
            case LEGS:
                return Optional.ofNullable(this.getBukkitEntity().getEquipment().getLeggings());
            case CHEST:
                return Optional.ofNullable(this.getBukkitEntity().getEquipment().getChestplate());
            case HEAD:
                return Optional.ofNullable(this.getBukkitEntity().getEquipment().getHelmet());
        }
        return Optional.empty();
    }

    @Override
    public @NotNull Object getEntity() {
        return this.entity;
    }

    @Override
    public @NotNull Optional<Object> getPathfinder() {
        return Optional.ofNullable(this.pathFinder);
    }

    @Override
    public void move(double x, double y, double z) {
        ((net.minecraft.server.v1_8_R3.Entity) this.entity).move(x, y, z);
    }

    @Override
    public void checkMovement(double x, double y, double z) {
        ((EntityHuman) this.entity).checkMovement(x, y, z);
    }

    public void onUpdate() {
        this.getTarget().ifPresent(target -> {
            if (target.isDead() || target instanceof Player && !((Player) target).isOnline()) {
                this.setTarget(null);
            } else if (this instanceof NPCPlayer && !((NPCPlayer) this).isLying() && this.getLocation().getWorld().equals(target.getWorld()) && this.getLocation().distanceSquared(target.getLocation()) <= 32 * 32) {
                if (target instanceof LivingEntity) {
                    this.lookAt(((LivingEntity) target).getEyeLocation());
                } else {
                    this.lookAt(target.getLocation());
                }
            }
        });
        this.getCurrentPath().ifPresent(currentPath -> {
            if (!currentPath.update()) {
                NPCPathFinishEvent event = new NPCPathFinishEvent(this, currentPath);
                Bukkit.getPluginManager().callEvent(event);
                this.checkActions(event);
                this.setCurrentPath(null);
            } else {
                Location toLook = currentPath.getDestination().clone();
                toLook.setY(this.getBukkitEntity().getEyeLocation().getY());
                this.lookAt(toLook);
            }
        });
    }

    public boolean onInteract(Object entity, ClickType clickType) {
        System.out.println("called oninteract for npc ");
        try {
            throw new Exception();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Player player = null;
        if (entity instanceof Player) player = (Player) entity;
        if (EntityHuman.class.equals(entity.getClass())) {
            player = (Player) ((EntityHuman) entity).getBukkitEntity();
        }
        if (player == null) return true;

        NPCInteractEvent event = new NPCInteractEvent(this, player, clickType);
        Bukkit.getPluginManager().callEvent(event);
        this.checkActions(event);
        return true;
    }

    public boolean onDamage(Object damageSource, Object entity, float damage) {
        if (isInvulnerable()) return false;
        String damageName = ((DamageSource) damageSource).translationIndex;

        EntityDamageEvent.DamageCause cause = EntityDamageEvent.DamageCause.CUSTOM;
        switch (damageName) {
            case "inFire":
                cause = EntityDamageEvent.DamageCause.FIRE;
                break;
            case "onFire":
                cause = EntityDamageEvent.DamageCause.FIRE_TICK;
                break;
            case "lava":
                cause = EntityDamageEvent.DamageCause.LAVA;
                break;
            case "inWall":
                cause = EntityDamageEvent.DamageCause.SUFFOCATION;
                break;
            case "drown":
                cause = EntityDamageEvent.DamageCause.DROWNING;
                break;
            case "starve":
                cause = EntityDamageEvent.DamageCause.STARVATION;
                break;
            case "cactus":
                cause = EntityDamageEvent.DamageCause.CONTACT;// TODO
                break;
            case "fall":
                cause = EntityDamageEvent.DamageCause.FALL;
                break;
            case "outOfWorld":
                cause = EntityDamageEvent.DamageCause.VOID;
                break;
            case "generic":
                //				System.out.println("generic");
                cause = EntityDamageEvent.DamageCause.CUSTOM;// TODO
                break;
            case "magic":
                cause = EntityDamageEvent.DamageCause.MAGIC;
                break;
            case "wither":
                cause = EntityDamageEvent.DamageCause.WITHER;
                break;
            case "anvil":
                cause = EntityDamageEvent.DamageCause.FALLING_BLOCK;// TODO
                break;
            case "fallingBlock":
                cause = EntityDamageEvent.DamageCause.FALLING_BLOCK;
                break;
            case "thorns":
                cause = EntityDamageEvent.DamageCause.THORNS;
                break;
            case "indirectMagic":// TODO Probably poison
                //				System.out.println("indirectMagic");
                break;
            case "fireball":
                //				System.out.println("fireball");
                cause = EntityDamageEvent.DamageCause.FIRE;// TODO
                break;
            case "thrown":
                //				System.out.println("thrown");
                cause = EntityDamageEvent.DamageCause.ENTITY_ATTACK;// TODO
                break;
            case "arrow":
                cause = EntityDamageEvent.DamageCause.PROJECTILE;// TODO
                break;
            case "mob":
                cause = EntityDamageEvent.DamageCause.ENTITY_ATTACK;
                break;
            case "player":
                cause = EntityDamageEvent.DamageCause.ENTITY_ATTACK;// TODO
                break;
            case "explosion.player":
                cause = EntityDamageEvent.DamageCause.ENTITY_EXPLOSION;// TODO
                break;
            case "explosion":
                cause = EntityDamageEvent.DamageCause.ENTITY_EXPLOSION;// TODO
                break;
            default:
                break;
        }

        Object damager = entity != null ? ((net.minecraft.server.v1_8_R3.Entity) entity).getBukkitEntity() : null;
        NPCDamageEvent event = new NPCDamageEvent(this, cause, (Entity) damager, damage);
        event.setCancelled(this.isInvulnerable());
        Bukkit.getPluginManager().callEvent(event);
        this.checkActions(event);
        return !event.isCancelled();
    }

    public boolean onCollide(@NotNull Entity collidedEntity) {
        if (!this.hasCollision() || this.isFrozen()) return false;
        NPCCollideEvent event = new NPCCollideEvent(this, collidedEntity);
        event.setCancelled(!this.hasCollision());
        Bukkit.getPluginManager().callEvent(event);
        this.checkActions(event);
        return !event.isCancelled();
    }

    public boolean onMotion(double x, double y, double z) {
        if (!this.hasCollision() || this.isFrozen()) return false;
        NPCMotionEvent event = new NPCMotionEvent(this, new Vector(x, y, z));
        event.setCancelled(this.isFrozen());
        Bukkit.getPluginManager().callEvent(event);
        this.checkActions(event);
        return !event.isCancelled();
    }

    public boolean onControl(@NotNull NPCControlEvent event) {
        if (!this.isControllable()) event.setCancelled(true);
        if (this.isFrozen() || this instanceof NPCPlayer && ((NPCPlayer) this).isLying()) event.setCancelled(true);
        if (!this.getPassenger().isPresent() || !(this.getPassenger().get() instanceof Player)) event.setCancelled(true);
        Bukkit.getPluginManager().callEvent(event);
        this.checkActions(event);
        return !event.isCancelled();
    }

    public boolean onDespawn() {
        NPCDespawnEvent event = new NPCDespawnEvent(this);
        Bukkit.getPluginManager().callEvent(event);
        this.checkActions(event);
        return true;
    }

    public void checkActions(@NotNull NPCEvent npcEvent) {
        for (ActionHandler actionHandler : this.getActionHandlers()) {
            actionHandler.handle(this, npcEvent);
        }
    }


    public void setCurrentPath(@Nullable Path path) {
        this.currentPath = path;
    }
}