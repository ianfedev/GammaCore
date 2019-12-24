package net.seocraft.api.bukkit.creator.v_1_8_R3.npc;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_8_R3.*;
import net.seocraft.api.bukkit.creator.npc.NPC;
import net.seocraft.api.bukkit.creator.npc.action.ClickType;
import net.seocraft.api.bukkit.creator.npc.entity.NPCEntityNMS;
import net.seocraft.api.bukkit.creator.npc.event.NPCControlEvent;
import net.seocraft.api.bukkit.creator.npc.navigation.Navigator;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public class NPCPlayerEntity_v1_8_R3 extends NPCPlayerEntityBase_v1_8_R3 {

    private static Field JUMP_FIELD;

    static {
        try {
            JUMP_FIELD = EntityLiving.class.getDeclaredField("aY");
            JUMP_FIELD.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public NPCPlayerEntity_v1_8_R3(@NotNull Plugin plugin, @NotNull Navigator navigator, @NotNull World world, @NotNull GameProfile gameProfile, @NotNull Location location) throws Exception {
        super(plugin, navigator, world, gameProfile);
        this.entity = new NPCEntityPlayer((MinecraftServer) this.minecraftServer, (WorldServer) this.worldServer, this.profile, (PlayerInteractManager) this.interactManager);
        this.initialize(world, location);
    }

    protected class NPCEntityPlayer extends EntityPlayer implements NPCEntityNMS {

        @Override
        public @NotNull NPC getNPC() {
            return NPCPlayerEntity_v1_8_R3.this;
        }

        public NPCEntityPlayer(MinecraftServer minecraftserver, WorldServer worldserver, GameProfile gameprofile, PlayerInteractManager playerinteractmanager) {
            super(minecraftserver, worldserver, gameprofile, playerinteractmanager);
        }

        @Override
        public void t_() {
            super.t_();
            this.K();

            if (!NPCPlayerEntity_v1_8_R3.this.isFrozen()) {
                this.motY = this.onGround ? Math.max(0.0, this.motY) : this.motY;
                this.move(this.motX, this.motY, this.motZ);
                this.motX *= 0.800000011920929;
                this.motY *= 0.800000011920929;
                this.motZ *= 0.800000011920929;
                if (NPCPlayerEntity_v1_8_R3.this.hasGravity() && !this.onGround) {
                    this.motY -= NPCPlayerEntity_v1_8_R3.this.getGravity();
                }
            }

            ((EntityLiving) this).m();

            NPCPlayerEntity_v1_8_R3.this.onUpdate();
        }

        @Override
        public boolean a(EntityHuman entityhuman) {
            if (NPCPlayerEntity_v1_8_R3.this.onInteract(entityhuman, ClickType.RIGHT_CLICK)) return super.a(entityhuman);
            return false;
        }

        @Override
        public boolean damageEntity(DamageSource damagesource, float f) {
            Object damager = null;
            if (damagesource instanceof EntityDamageSource) {
                damager = ((EntityDamageSource) damagesource).getEntity();
            }
            if (NPCPlayerEntity_v1_8_R3.this.onDamage(damagesource, damager, f)) return super.damageEntity(damagesource, f);
            return false;
        }

        @Override
        public void collide(Entity arg0) {
            if (NPCPlayerEntity_v1_8_R3.this.onCollide(arg0.getBukkitEntity())) {
                super.collide(arg0);
            }
        }

        @Override
        public void g(double d0, double d1, double d2) {
            if (NPCPlayerEntity_v1_8_R3.this.onMotion(d0, d1, d2)) {
                super.g(d0, d1, d2);
            }
        }

        @Override
        public void g(float motionSide, float motionForward) {
            if (this.passenger == null || !(this.passenger instanceof EntityHuman)) {
                super.g(motionSide, motionForward);
                this.S = 0.5f;
                return;
            }

            motionSide = ((EntityHuman) this.passenger).aZ * 0.5f;
            motionForward = ((EntityHuman) this.passenger).ba;

            if (motionForward <= 0f) {
                motionForward *= 0.25f;
            }
            motionSide *= 0.75f;

            if (NPCPlayerEntity_v1_8_R3.this.getPassenger().isPresent()) {
                NPCControlEvent event = new NPCControlEvent(NPCPlayerEntity_v1_8_R3.this, (Player) NPCPlayerEntity_v1_8_R3.this.getPassenger().get(), motionSide, motionForward);
                if (!NPCPlayerEntity_v1_8_R3.this.onControl(event)) return;
                if (event.isCancelled()) return;

                try {
                    if (JUMP_FIELD.getBoolean(this.passenger) && this.onGround) {
                        this.motY += 0.45F;
                        this.ai = true;
                        if (motionForward > 0.0F) {
                            float f2 = MathHelper.sin(this.yaw * 3.141593F / 180.0F);
                            float f3 = MathHelper.cos(this.yaw * 3.141593F / 180.0F);

                            this.motX += -0.4F * f2 * ((EntityLiving) this).br();
                            this.motZ += 0.4F * f3 * ((EntityLiving) this).br();
                        }
                    }
                } catch (IllegalAccessException ex) {
                    ex.printStackTrace();
                }

                motionSide = event.getSidewaysMotion();
                motionForward = event.getForwardMotion();

                this.lastYaw = this.yaw = this.passenger.yaw;
                this.pitch = this.passenger.pitch - 0.5f;

                this.setYawPitch(this.yaw, this.pitch);
                this.aK = this.aI = this.yaw;

                this.S = 1.0f;

                float speed = 0.25f;
                this.k(speed);
                super.g(motionSide, motionForward);
            }
        }

        @Override
        public void die() {
            if (NPCPlayerEntity_v1_8_R3.this.onDespawn()) {
                super.die();
            }
        }
    }
}