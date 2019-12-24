package net.seocraft.api.bukkit.creator.v_1_8_R3.npc.navigation;

import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.PathEntity;
import net.minecraft.server.v1_8_R3.Vec3D;
import net.seocraft.api.bukkit.creator.npc.NPC;
import net.seocraft.api.bukkit.creator.npc.entity.NPCEntity;
import net.seocraft.api.bukkit.creator.npc.entity.player.NPCPlayer;
import net.seocraft.api.bukkit.creator.npc.navigation.Path;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

@Getter
public class CraftPath_v1_8_R3 implements Path {

    @NotNull private NPC npc;
    @NotNull Location destination;
    @NotNull private Object pathEntity;

    @NotNull Object currentNmsPoint;
    @Getter(AccessLevel.NONE)
    @NotNull private Vector currentPoint;

    private double speed;
    private double progress;

    public CraftPath_v1_8_R3(@NotNull NPC npc, @NotNull Location destination, @NotNull Object pathEntity, double speed) {
        this.npc = npc;
        this.destination = destination;
        this.pathEntity = pathEntity;
        this.speed = speed;
        this.progress = 0D;
        this.currentNmsPoint = ((PathEntity) pathEntity).a((Entity) ((NPCEntity) npc).getEntity());
        this.currentPoint = this.getCurrentPoint();
    }

    @Override
    public @NotNull Vector getCurrentPoint() {
        Vector vector = new Vector();

        vector.setX(((Vec3D) this.currentNmsPoint).a);
        vector.setY(((Vec3D) this.currentNmsPoint).b);
        vector.setZ(((Vec3D) this.currentNmsPoint).c);

        return vector;
    }

    @Override
    public boolean update() {
        int current = this.floor(this.progress);
        double d = this.progress - current;
        double d1 = 1 - d;

        if (d + this.speed < 1) {
            double dx = (this.currentPoint.getX() - this.npc.getLocation().getX()) * this.speed;
            double dz = (this.currentPoint.getZ() - this.npc.getLocation().getZ()) * this.speed;

            if (this.npc.hasCollision()) {
                dx += Math.random() / 10;
                dx += Math.random() / 10;
            }

            ((NPCEntity) this.npc).move(dx, 0, dz);
            if (this.npc instanceof NPCPlayer) {
                ((NPCEntity) this.npc).checkMovement(dx, 0, dz);
            }
            this.progress += this.speed;
        } else {
            double bx = (this.currentPoint.getX() - this.npc.getLocation().getX()) * d1;
            double bz = (this.currentPoint.getZ() - this.npc.getLocation().getZ()) * d1;

            ((PathEntity) this.pathEntity).a();
            if (!((PathEntity) this.pathEntity).b()) {
                this.currentNmsPoint = ((PathEntity) this.pathEntity).a((Entity) ((NPCEntity) this.npc).getEntity());
                this.currentPoint = this.getCurrentPoint();

                double d2 = this.speed - d1;

                double dx = bx + (this.currentPoint.getX() - this.npc.getLocation().getX()) * d2;
                double dy = this.currentPoint.getY() - this.npc.getLocation().getY();
                double dz = bz + (this.currentPoint.getZ() - this.npc.getLocation().getZ()) * d2;

                if (this.npc.hasCollision()) {
                    dx += Math.random() / 10;
                    dx += Math.random() / 10;
                }

                ((NPCEntity) this.npc).move(dx, dy, dz);
                if (this.npc instanceof NPCPlayer) {
                    ((NPCEntity) this.npc).checkMovement(dx, dy, dz);
                }
                this.progress += this.speed;
            } else {
                ((NPCEntity) this.npc).move(bx, 0, bz);
                if (this.npc instanceof NPCPlayer) {
                    ((NPCEntity) this.npc).checkMovement(bx, 0, bz);
                }
                return false;
            }
        }
        return true;
    }

    private int floor(double d1) {
        int i = (int) d1;
        return d1 >= i ? i : i - 1;
    }
}